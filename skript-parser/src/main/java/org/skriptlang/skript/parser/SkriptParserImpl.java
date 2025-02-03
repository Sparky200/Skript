package org.skriptlang.skript.parser;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.skriptlang.skript.api.*;
import org.skriptlang.skript.api.entries.*;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.scope.InputDefinition;
import org.skriptlang.skript.api.scope.SectionScope;
import org.skriptlang.skript.api.script.ScriptSource;
import org.skriptlang.skript.api.util.LockAccess;
import org.skriptlang.skript.api.util.ResultWithDiagnostics;
import org.skriptlang.skript.api.util.ScriptDiagnostic;
import org.skriptlang.skript.parser.pattern.SyntaxPatternElement;
import org.skriptlang.skript.parser.tokens.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * The JVM implementation of the Skript Parser.
 * <p>
 * This is no-man's land. I wish you luck, brave soul.
 */
public final class SkriptParserImpl implements SkriptParser {
	private final LockAccess lockAccess;

	private final List<SyntaxNodeType<?>> nodeTypes = new LinkedList<>();

	/**
	 * The tokenized syntaxes that have been generated from the node types.
	 * This is lazily computed and cached on the first parse after lock.
	 * <p>
	 * Note this is not directly used during parse-time.
	 * This will be copied into a context object.
	 */
	private @Nullable List<TokenizedSyntax> tokenizedSyntaxes = null;

	// TODO: this can and should be lazily computed just like tokenized syntaxes
	private final List<SectionScope> scopes = new LinkedList<>();

	public SkriptParserImpl(@NotNull LockAccess lockAccess) {
		Preconditions.checkNotNull(lockAccess, "lockAccess cannot be null");
		Preconditions.checkArgument(!lockAccess.isLocked(), "lockAccess must not be locked on construction of parser");
		this.lockAccess = lockAccess;
	}

	@Override
	public void submitNode(@NotNull SyntaxNodeType<?> nodeType) {
		Preconditions.checkNotNull(nodeType, "nodeType cannot be null");

		if (lockAccess.isLocked()) {
			throw new IllegalStateException("The parser is locked and cannot accept new node types.");
		}

		synchronized (nodeTypes) {
			nodeTypes.add(nodeType);
			tokenizedSyntaxes = null;
		}
	}

	@Override
	public void submitScope(@NotNull SectionScope scope) {
		Preconditions.checkNotNull(scope, "scope cannot be null");

		if (lockAccess.isLocked()) {
			throw new IllegalStateException("The parser is locked and cannot accept new scopes.");
		}

		synchronized (scopes) {
			if (scopes.stream().anyMatch(s -> s.name().equals(scope.name()))) {
				throw new IllegalArgumentException("Scope with name " + scope.name() + " already exists");
			}
			scopes.add(scope);
		}
	}

	@Override
	public boolean isLocked() {
		return lockAccess.isLocked();
	}

	@Contract(pure = true)
	@Override
	public @NotNull @UnmodifiableView List<SyntaxNodeType<?>> getNodeTypes() {
		return Collections.unmodifiableList(nodeTypes);
	}

	@Override
	public @NotNull ResultWithDiagnostics<SectionNode> parse(@NotNull ScriptSource source) {
		if (!lockAccess.isLocked()) {
			throw new IllegalStateException("The parser is not locked and cannot parse.");
		}

		if (tokenizedSyntaxes == null) {
			var computeResult = computeTokenizedSyntaxes();
			if (!computeResult.isSuccess()) {
				return ResultWithDiagnostics.failure(computeResult.getDiagnostics());
			}
		}

		var tokenizeResult = Tokenizer.tokenize(source);

		// will be joint between token diagnostics and parse diagnostics
		var diagnostics = new LinkedList<>(tokenizeResult.getDiagnostics());

		if (!tokenizeResult.isSuccess()) {
			diagnostics.add(ScriptDiagnostic.error(source, "Script could not be tokenized"));
			return ResultWithDiagnostics.failure(diagnostics);
		}

		var tokens = tokenizeResult.get();

		ParseContextImpl parseContextImpl = new ParseContextImpl(source);
		pushParseableSyntaxes(parseContextImpl);

		var fileNode = parseSection(parseContextImpl, null, tokens);

		if (parseContextImpl.depth() != -1) {
			diagnostics.add(ScriptDiagnostic.error(source, "Unbalanced sections (a section did not pop)", tokens.getLast().start()));
			return ResultWithDiagnostics.failure(diagnostics);
		}

		if (fileNode == null) {
			diagnostics.add(ScriptDiagnostic.error(source, "Script could not be parsed"));
			return ResultWithDiagnostics.failure(diagnostics);
		}

		return ResultWithDiagnostics.success(fileNode.node());
	}

	/**
	 * Parses a section.
	 * @param parseContextImpl The parse context we are currently in.
	 * @param tokens The tokens to parse.
	 * @return The parsed section, or null if failed.
	 */
	private @Nullable Match<SectionNode> parseSection(
		ParseContextImpl parseContextImpl,
		@Nullable SectionScope scope,
		List<Token> tokens
	) {
		// at head, we are just after a colon

		int index = 0;

		// if this section isn't the root of the file, it must start with certain whitespace rules
		parseContextImpl.pushSection(parseContextImpl.depth() != -1 ? tokens.get(index++) : null, scope);
		if (scope != null) pushInputs(parseContextImpl, scope.inputs());

		// depth is now 0

		List<StatementNode> statements = new LinkedList<>();

		Token whitespace;
		do {
			if (index >= tokens.size()) break;
			Match<StatementNode> next = parseStatement(parseContextImpl, tokens.subList(index, tokens.size()), parseContextImpl.depth() == 0 ? StructureNodeType.class : EffectNodeType.class);
			if (next == null) {
				parseContextImpl.info("Fail occurred in section depth " + parseContextImpl.depth(), tokens.get(index).start());
				parseContextImpl.popSection();
				return null;
			}
			index += next.length();
			statements.add(next.node());
			if (index >= tokens.size()) break;
			whitespace = tokens.get(index);
			// statement might have consumed the whitespace
			if (whitespace.type() != TokenType.WHITESPACE) whitespace = tokens.get(--index);
			if (whitespace.type() != TokenType.WHITESPACE || !whitespace.asString().contains("\n")) {
				parseContextImpl.error("Expected newline after effect", tokens.get(index).start());
				parseContextImpl.popSection();
				return null;
			}
			index++;
		} while (whitespace.asString().substring(whitespace.asString().lastIndexOf('\n') + 1).length() == parseContextImpl.currentSection().getIndent());

		if (scope != null) parseContextImpl.popSyntaxFrame();
		parseContextImpl.popSection();
		return new Match<>(new SectionNode(statements), index);
	}

	/**
	 * Parses an effect. Only effects which consume an entire line will be allowed to succeed.
	 * @param parseContextImpl The parse stack containing the source, diagnostics, and other context on current parsing.
	 * @param tokens The tokens to parse.
	 * @param superType The super type to bound candidates to.
	 * @return The parsed effect, or null if failed.
	 */
	private @Nullable Match<StatementNode> parseStatement(
		@NotNull ParseContextImpl parseContextImpl,
		List<Token> tokens,
		Class<?> superType
	) {
		int end = tokens.stream()
			.map(token -> token.type() == TokenType.WHITESPACE && token.asString().contains("\n"))
			.toList()
			.indexOf(true);

		// edge case: this is the last line of the script, and there is no newline. therefore, the effect goes to the end.
		if (end == -1) end = tokens.size();

		if (end == 0) {
			// normal; there is not another effect
			return null;
		}

		List<Token> singleLineTokens = tokens.subList(0, Math.min(end + 1, tokens.size()));
		// This edge case shouldn't even occur
		// because the tokenizer does not output duplicate newline-containing whitespace tokens
		if (singleLineTokens.isEmpty()) {
			parseContextImpl.error("Expected effect", tokens.getFirst().start());
			return null;
		}

		List<TokenizedSyntax> candidates = findCandidates(parseContextImpl, singleLineTokens, superType);

		if (candidates.isEmpty()) {
			parseContextImpl.error("No statement matched", tokens.getFirst().start());
			return null;
		}

		int finalEnd = end;
		Stream<Match<SyntaxNode>> candidateNodes = candidates.stream()
			// attempt to parse each candidate
			.map(candidate -> parseCandidate(parseContextImpl, candidate, tokens.subList(0, hasSection(candidate) ? tokens.size() : finalEnd)))
			// filter to successful parses
			.filter(Objects::nonNull);

		// statements are always greedy
		return candidateNodes
			// filter to matches that are statement nodes (just in case)
			.filter(match -> match.node() instanceof StatementNode)
			// then, the longest match wins
			.max(Comparator.comparingInt(Match::length))
			// then, return a cast match if present
			.map(m -> new Match<>((StatementNode) m.node(), m.length()))
			// or else, return null
			.orElse(null);
	}

	private Match<SyntaxNode> parseCandidate(
		@NotNull ParseContextImpl context,
		@NotNull TokenizedSyntax candidate,
		@NotNull List<Token> tokens
	) {

		List<Token> syntaxTokens = candidate.tokens();

		List<SyntaxNode> children = new LinkedList<>();

		int tokenIndex = 0;
		for (int i = 0; i < syntaxTokens.size(); i++) {
			Token syntaxToken = syntaxTokens.get(i);
			if (syntaxToken.type() != TokenType.SYNTAX) {
				tokenIndex++;
				continue;
			}

			SyntaxPatternElement element = (SyntaxPatternElement) syntaxToken.value();

			List<Token> subTokens;
			if (element.syntaxType().equals("section") || element.syntaxType().equals("entries")) {
				if (i != syntaxTokens.size() - 1) {
					context.error("Section or entries syntax must be at the end of a pattern", syntaxToken.start());
					return null;
				}
				subTokens = tokens.subList(tokenIndex, tokens.size());
			} else {
				subTokens = tokens.subList(tokenIndex, tokenIndex + findEnd(
					tokens.subList(tokenIndex, tokens.size()),
					syntaxTokens.subList(i + 1, syntaxTokens.size())
				));
				if (
					!subTokens.isEmpty()
						&& subTokens.getFirst().asPunctuation() == Punctuation.OPEN_PARENTHESIS
						&& subTokens.getLast().asPunctuation() == Punctuation.CLOSE_PARENTHESIS) {
					subTokens = subTokens.subList(1, subTokens.size() - 1);
					tokenIndex += 2;
				}
			}

			Match<? extends SyntaxNode> match = parseChild(context, candidate, subTokens, element, children.size());
			if (match == null) {
				context.error("Failed to parse child syntax", subTokens.getFirst().start());
				return null;
			}

			children.add(match.node());
			tokenIndex += match.length();
		}

		if (tokenIndex != tokens.size() && !hasSection(candidate)) {
			context.error("Failed to parse all tokens", tokens.get(tokenIndex).start());
			return null;
		}

		return new Match<>(candidate.nodeType().create(children, candidate.patternIndex()), tokenIndex);
	}

	private int findEnd(
		@NotNull List<Token> tokens,
		@NotNull List<Token> syntaxTokens
	) {
		if (syntaxTokens.isEmpty() || syntaxTokens.stream().allMatch(it -> it.type() == TokenType.SYNTAX)) {
			return tokens.size();
		}

		if (tokens.getFirst().asPunctuation() == Punctuation.OPEN_PARENTHESIS) {
			Deque<Punctuation> parenStack = new LinkedList<>();
			for (int i = 1; i < tokens.size(); i++) {
				Token token = tokens.get(i);
				if (token.asPunctuation() == Punctuation.OPEN_PARENTHESIS) {
					parenStack.push(Punctuation.OPEN_PARENTHESIS);
				} else if (token.asPunctuation() == Punctuation.CLOSE_PARENTHESIS) {
					if (parenStack.isEmpty()) {
						return i + 1;
					}
					parenStack.pop();
				}
			}
		}

		for (int i = 0; i < tokens.size(); i++) {
			if (TokenComparer.canMatch(syntaxTokens, tokens.subList(i, tokens.size()))) {
				return i;
			}
		}

		return tokens.size();
	}

	private @Nullable Match<? extends SyntaxNode> parseChild(
		@NotNull ParseContextImpl context,
		@NotNull TokenizedSyntax parentCandidate,
		@NotNull List<Token> tokens,
		@NotNull SyntaxPatternElement childElement,
		int childIndex
	) {
		boolean hasInputs = !childElement.syntaxType().equals("section") && !childElement.inputs().isEmpty();

		if (hasInputs) pushInputs(context, childElement.inputs());
		pushParseableSyntaxes(context);

		context.push(new ParseContextImpl.Context(parentCandidate.nodeType(), childIndex));

		Match<? extends SyntaxNode> match = null;
		switch (childElement.syntaxType()) {
			case "section" -> {
				SectionScope scope = !childElement.inputs().isEmpty()
					? scopes.stream()
						.filter(s -> s.name().equals(childElement.inputs().getFirst().name()))
						.findFirst().orElse(null)
					: null;

				match = parseSection(context, scope, tokens);
			}
			case "entries" -> {
				if (!(parentCandidate.nodeType() instanceof StructureNodeType<?> structureType)) break;
				match = parseEntries(context, tokens, structureType.structure());
			}
			case "effect" -> {
				match = parseStatement(context, tokens, ExpressionNodeType.class);
			}
			case "expr" -> {
				match = parseExpression(context, tokens, childElement.output());
			}
			case "token" -> {
				if (tokens.size() != 1) break;
				Token token = tokens.getFirst();
				if (token.type() != TokenType.fromName(childElement.output())) break;

				if (token.type() == TokenType.STRING) {
					// special case for string tokens
					List<ExpressionNode<?>> children = new LinkedList<>();
					// TODO: string template context
					for (List<Token> subTokens : Objects.requireNonNull(token.children())) {
						Match<ExpressionNode<?>> expression = parseExpression(context, subTokens, "string");
						if (expression == null) {
							context.error("Failed to parse string template expression", subTokens.getFirst().start());
							return null;
						}
						children.add(expression.node());
					}
					match = new Match<>(new StringNode(token.asString(), children), 1);
				} else
					match = new Match<>(new TokenNode(tokens.getFirst().asString()), 1);
			}
			default -> {
				context.error("Unknown syntax type '" + childElement.syntaxType() + "' in pattern " + parentCandidate.patternIndex() + " of " + parentCandidate.nodeType(), tokens.getFirst().start());
			}
		}

		context.pop();

		context.popSyntaxFrame();
		if (hasInputs) context.popSyntaxFrame();

		return match;
	}

	private Match<ExpressionNode<?>> parseExpression(
		@NotNull ParseContextImpl context,
		@NotNull List<Token> tokens,
		@Nullable String desiredTypeName
	) {
		List<TokenizedSyntax> candidates = findCandidates(context, tokens, ExpressionNodeType.class);
		if (candidates.isEmpty()) {
			context.error("No possible expression", tokens.getFirst().start());
			return null;
		}

		List<Match<SyntaxNode>> candidateNodes = candidates.stream()
			.map(candidate -> parseCandidate(context, candidate, tokens))
			.filter(Objects::nonNull)
			.filter(it -> it.length() == tokens.size())
			.toList();

		if (candidateNodes.size() > 1) {
			return new Match<>(new MultiMatchExpressionNode(
				candidateNodes.stream().map(it -> (ExpressionNode<?>) it.node()).toList(),
				desiredTypeName
			), tokens.size());
		}

		Match<SyntaxNode> selected = candidateNodes.stream().findFirst().orElse(null);
		if (selected == null) return null;
		return new Match<>((ExpressionNode<?>) selected.node(), selected.length());
	}

	private Match<StructureSectionNode> parseEntries(
		@NotNull ParseContextImpl context,
		@NotNull List<Token> tokens,
		@Nullable EntryStructureDefinition structure
	) {
		if (structure == null) return null;

		Map<String, StructureEntryNode> entries = new LinkedHashMap<>();
		Map<String, EntryDefinition> used = new LinkedHashMap<>();
		Map<String, EntryDefinition> unused = new LinkedHashMap<>();

		structure.entries().forEach(it -> unused.put(it.name(), it));

		context.pushSection(tokens.getFirst(), null);
		context.pushSyntaxFrame(
			unused.values().stream()
				.map(StructureEntryNodeType::of)
				// syntax allowed to use all features
				.flatMap(nodeType -> {
					var source = new SyntaxScriptSource(nodeType.getClass().getSimpleName(), nodeType.getSyntaxes().getFirst());
					var result = Tokenizer.tokenizeSyntax(source, nodeType, 0);
					if (!result.isSuccess()) {
						throw new IllegalStateException("Fatal edge case: entry tokenization failed");
					}
					return result.get().stream();
				})
				.toList()
		);

		// start after whitespace
		int index = 1;

		Token whitespace;
		do {
			if (index >= tokens.size()) break;
			Match<StructureEntryNode> next = parseEntry(context, tokens.subList(index, tokens.size()), unused);
			if (next == null) {
				context.info("Fail occurred in section depth " + context.depth(), tokens.get(index).start());
				context.popSection();
				return null;
			}
			index += next.length();
			entries.put(next.node().name(), next.node());
			EntryDefinition def = unused.remove(next.node().name());
			used.put(next.node().name(), def);

			if (index >= tokens.size()) break;
			whitespace = tokens.get(index);
			// statement might have consumed the whitespace
			if (whitespace.type() != TokenType.WHITESPACE) whitespace = tokens.get(--index);
			if (whitespace.type() != TokenType.WHITESPACE || !whitespace.asString().contains("\n")) {
				context.error("Expected newline after effect", tokens.get(index).start());
				context.popSection();
				context.popSyntaxFrame();
				return null;
			}
			index++;
		} while (whitespace.asString().substring(whitespace.asString().lastIndexOf('\n') + 1).length() == context.currentSection().getIndent());

		context.popSyntaxFrame();
		context.popSection();

		if (unused.values().stream().anyMatch(entry -> !entry.optional())) {
			context.error("Missing required entries: " + unused.values().stream()
					.filter(entry -> !entry.optional())
					.map(EntryDefinition::name)
					.toList(),
				tokens.get(index).start()
			);
			return null;
		}

		return new Match<>(new StructureSectionNode(entries), tokens.size());
	}

	private Match<StructureEntryNode> parseEntry(
		@NotNull ParseContextImpl context,
		@NotNull List<Token> tokens,
		@NotNull Map<String, EntryDefinition> unused
	) {
		List<TokenizedSyntax> candidates = findCandidates(context, tokens, StructureEntryNodeType.class);

		if (candidates.isEmpty()) {
			context.error("No entry matched", tokens.getFirst().start());
			return null;
		}

		return candidates.stream()
			.map(candidate -> parseCandidate(context, candidate, hasSection(candidate) ? tokens : tokens.subList(0, tokens.size() - 1)))
			.filter(Objects::nonNull)
			.map(node -> {
				if (node.node() instanceof StructureEntryNode entryNode)
					return new Match<>(entryNode, node.length());
				return null;
			})
			.filter(Objects::nonNull)
			.filter(match -> {
				EntryDefinition definition = unused.get(match.node().name());
				if (definition == null) {
					context.error("Entry " + match.node().name() + " is not allowed", tokens.getFirst().start());
					return false;
				}
				// TODO: definition.validate(...) behavior may be interesting
				return true;
			})
			.findFirst()
			.orElse(null);
	}

	private void pushInputs(@NotNull ParseContextImpl parseContextImpl, List<InputDefinition> inputs) {
		parseContextImpl.pushSyntaxFrame(inputs.stream().map(input -> {
			InputNode.Type type = new InputNode.Type(input.name(), input.type());
			var result = Tokenizer.tokenizeSyntax(new SyntaxScriptSource("input", input.name()), type, 0);
			if (!result.isSuccess()) {
				throw new IllegalStateException("Fatal edge case: input tokenization failed");
			}
			return result.get().getFirst();
		}).toList());
	}

	private boolean hasSection(@NotNull TokenizedSyntax syntax) {
		return syntax.tokens().stream().anyMatch(it -> {
			if (it.type() != TokenType.SYNTAX) return false;
			SyntaxPatternElement element = (SyntaxPatternElement) it.value();
			return element.syntaxType().equals("section") || element.syntaxType().equals("entries");
		});
	}

	/**
	 * Finds the candidates that can match the given tokens that are of the given super type.
	 * @param tokens The tokens to match against.
	 * @param superType The super type to bound candidates to.
	 * @return The candidates that can match the tokens.
	 */
	private @NotNull List<TokenizedSyntax> findCandidates(
		@NotNull ParseContextImpl context,
		@NotNull List<Token> tokens,
		@NotNull Class<?> superType
	) {
		return context.availableSyntaxes().stream()
			.filter(tokenizedSyntax -> superType.isInstance(tokenizedSyntax.nodeType()))
			.filter(tokenizedSyntax -> tokenizedSyntax.canMatch(tokens))
			.filter(tokenizedSyntax -> tokenizedSyntax.nodeType().canBeParsed(context, tokenizedSyntax.patternIndex()))
			.toList();
	}

	private void pushParseableSyntaxes(@NotNull ParseContextImpl context) {
		List<TokenizedSyntax> alreadyAvailable = context.availableSyntaxes();
		context.pushSyntaxFrame(
			Objects.requireNonNull(tokenizedSyntaxes).stream()
				.filter(it ->
					!alreadyAvailable.contains(it)
						&& it.nodeType().canBeParsed(context, it.patternIndex())
				)
				.toList()
		);
	}

	/**
	 * Lazily computes the tokenized syntaxes from the node types.
	 */
	private synchronized ResultWithDiagnostics<Object> computeTokenizedSyntaxes() {
		if (tokenizedSyntaxes != null) return ResultWithDiagnostics.success(new Object());
		if (!isLocked()) throw new IllegalStateException("Cannot compute tokenized syntaxes because the parser is not locked");

		var list = new LinkedList<TokenizedSyntax>();

		for (var nodeType : nodeTypes) {

			var syntaxes = nodeType.getSyntaxes();
			if (syntaxes == null) continue;
			for (var syntax : syntaxes) {

				var source = new SyntaxScriptSource(nodeType.getClass().getSimpleName(), syntax);

				var tokens = Tokenizer.tokenizeSyntax(source, nodeType, syntaxes.indexOf(syntax));

				if (!tokens.isSuccess()) {
					var diagnostics = new LinkedList<>(tokens.getDiagnostics());
					diagnostics.add(ScriptDiagnostic.error(source, "Syntax from " + nodeType + " could not be tokenized"));
					return ResultWithDiagnostics.failure(diagnostics);
				}

				list.addAll(tokens.get());
			}
		}

		tokenizedSyntaxes = Collections.unmodifiableList(list);
		return ResultWithDiagnostics.success(new Object());
	}

	private record SyntaxScriptSource(String name, String content) implements ScriptSource {}

	private record Match<T extends SyntaxNode>(T node, int length) { }
}
