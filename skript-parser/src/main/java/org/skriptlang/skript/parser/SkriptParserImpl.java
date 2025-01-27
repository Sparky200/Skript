package org.skriptlang.skript.parser;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.skriptlang.skript.api.*;
import org.skriptlang.skript.api.entries.*;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.scope.SectionScope;
import org.skriptlang.skript.api.script.ScriptSource;
import org.skriptlang.skript.api.util.ResultWithDiagnostics;
import org.skriptlang.skript.api.util.ScriptDiagnostic;
import org.skriptlang.skript.parser.pattern.SyntaxPatternElement;
import org.skriptlang.skript.parser.tokens.Token;
import org.skriptlang.skript.parser.tokens.TokenComparer;
import org.skriptlang.skript.parser.tokens.TokenType;
import org.skriptlang.skript.parser.tokens.Tokenizer;

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

		ParseContext parseContext = new ParseContext(source, diagnostics);
		parseContext.pushSyntaxFrame(tokenizedSyntaxes);

		var fileNode = parseSection(parseContext, null, tokens, 0);

		if (parseContext.depth() != -1) {
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
	 * @param parseContext The parse stack containing the source, diagnostics, and other context on current parsing.
	 * @param tokens The tokens to parse.
	 * @param start The index to start parsing at.
	 * @return The parsed section, or null if failed.
	 */
	private @Nullable Match<SectionNode> parseSection(
		ParseContext parseContext,
		@Nullable SectionScope scope,
		List<Token> tokens,
		int start
	) {
		// at head, we are just after a colon

		int index = start;

		// if this section isn't the root of the file, it must start with certain whitespace rules
		parseContext.pushSection(parseContext.depth() != -1 ? tokens.get(index++) : null, scope);

		// depth is now 0

		List<StatementNode> statements = new LinkedList<>();

		Token whitespace;
		do {
			if (index >= tokens.size()) break;
			Match<StatementNode> next = parseStatement(parseContext, tokens, index, parseContext.depth() == 0 ? StructureNodeType.class : EffectNodeType.class);
			if (next == null) {
				parseContext.info("Fail occurred in section depth " + parseContext.depth(), tokens.get(index).start());
				parseContext.popSection();
				return null;
			}
			index += next.length();
			statements.add(next.node());
			if (index >= tokens.size()) break;
			whitespace = tokens.get(index);
			// statement might have consumed the whitespace
			if (whitespace.type() != TokenType.WHITESPACE) whitespace = tokens.get(--index);
			if (whitespace.type() != TokenType.WHITESPACE || !whitespace.asString().contains("\n")) {
				parseContext.error("Expected newline after effect", tokens.get(index).start());
				parseContext.popSection();
				return null;
			}
			index++;
		} while (whitespace.asString().substring(whitespace.asString().lastIndexOf('\n') + 1).length() == parseContext.currentSection().getIndent());

		parseContext.popSection();
		return new Match<>(new SectionNode(statements), index - start);
	}

	private @Nullable Match<EntryStructureSectionNode> parseEntrySection(
		@NotNull ParseContext parseContext,
		@Nullable SectionScope scope,
		@NotNull List<Token> tokens,
		int start
	) {
		SyntaxNodeType<?> structureSyntax = parseContext.currentContext().peek();
		if (!(structureSyntax instanceof StructureNodeType<?> structure)) {
			parseContext.error("Only structures can have entries", tokens.get(start).start());
			return null;
		}

		EntryStructureDefinition definition = structure.structure();
		if (definition == null) {
			parseContext.error("Structure " + structure +
				" must define a structure definition because it contains a syntax with <entries>",
				tokens.get(start).start());
			return null;
		}

		// defs will be removed from this as they are used
		List<EntryDefinition> unusedEntries = new LinkedList<>(definition.entries());
		List<EntryDefinition> usedEntries = new LinkedList<>();

		Map<String, StructureEntryNode> entries = new LinkedHashMap<>();

		int index = start;

		parseContext.pushSection(tokens.get(index++), scope);
		parseContext.pushSyntaxFrame(
			unusedEntries.stream()
				.map(StructureEntryNodeType::of)
				// syntax allowed to use all features
				.flatMap(nodeType -> {
					var source = new SyntaxScriptSource(nodeType.getClass().getSimpleName(), nodeType.getSyntaxes().getFirst());
					var result = Tokenizer.tokenizeSyntax(source, nodeType);
					if (!result.isSuccess()) {
						throw new IllegalStateException("Fatal edge case: entry tokenization failed");
					}
					return result.get().stream();
				})
				.toList()
		);

		Token whitespace;
		do {
			if (index >= tokens.size()) break;
			Match<StructureEntryNode> next = parseEntry(parseContext, tokens, index, unusedEntries);
			if (next == null) {
				parseContext.info("Fail occurred in section depth " + parseContext.depth(), tokens.get(index).start());
				parseContext.popSection();
				parseContext.popSyntaxFrame();
				return null;
			}

			index += next.length();
			EntryDefinition usedEntry = unusedEntries.stream()
				.filter(entry -> entry.name().equals(next.node().name()))
				.findFirst().orElse(null);

			if (usedEntry == null) throw new IllegalStateException("parseEntry should validate that the entry is allowed");

			usedEntries.add(usedEntry);
			unusedEntries.remove(usedEntry);

			entries.put(next.node().name(), next.node());
			if (index >= tokens.size()) break;
			whitespace = tokens.get(index);
			// entry might have consumed the whitespace
			if (whitespace.type() != TokenType.WHITESPACE) whitespace = tokens.get(--index);
			if (whitespace.type() != TokenType.WHITESPACE || !whitespace.asString().contains("\n")) {
				parseContext.error("Expected newline after effect", tokens.get(index).start());
				parseContext.popSection();
				parseContext.popSyntaxFrame();
				return null;
			}
			index++;
		} while (whitespace.asString().substring(whitespace.asString().lastIndexOf('\n') + 1).length() == parseContext.currentSection().getIndent());

		parseContext.popSection();
		parseContext.popSyntaxFrame();

		if (unusedEntries.stream().anyMatch(entry -> !entry.optional())) {
			parseContext.error("Missing required entries: " + unusedEntries.stream()
				.filter(entry -> !entry.optional())
				.map(EntryDefinition::name)
				.toList(),
				tokens.get(index).start()
			);
			return null;
		}

		return new Match<>(new EntryStructureSectionNode(entries), index - start);
	}

	private Match<StructureEntryNode> parseEntry(@NotNull ParseContext parseContext, @NotNull List<Token> tokens, int index, List<EntryDefinition> allowedEntries) {
		List<TokenizedSyntax> candidates = findCandidates(parseContext, tokens.subList(index, tokens.size()), StructureEntryNodeType.class);

		if (candidates.isEmpty()) {
			parseContext.error("No entry matched", tokens.get(index).start());
			return null;
		}

		return candidates.stream()
			.map(candidate -> tryParse(parseContext, candidate, tokens.subList(index, tokens.size())))
			.filter(Objects::nonNull)
			.map(node -> {
				if (node.node() instanceof StructureEntryNode entryNode)
					return new Match<>(entryNode, node.length());
				return null;
			})
			.filter(Objects::nonNull)
			.filter(match -> {
				EntryDefinition definition = allowedEntries.stream().filter(entry -> entry.name().equals(match.node().name())).findFirst().orElse(null);
				if (definition == null) {
					parseContext.error("Entry " + match.node().name() + " is not allowed", tokens.get(index).start());
					return false;
				}
				// TODO: definition.validate(...) behavior may be interesting
				return true;
			})
			.findFirst()
			.orElse(null);
	}

	/**
	 * Parses an effect. Only effects which consume an entire line will be allowed to succeed.
	 * @param parseContext The parse stack containing the source, diagnostics, and other context on current parsing.
	 * @param tokens The tokens to parse.
	 * @param start The index to start parsing at.
	 * @param superType The super type to bound candidates to.
	 * @return The parsed effect, or null if failed.
	 */
	private @Nullable Match<StatementNode> parseStatement(
		@NotNull ParseContext parseContext,
		List<Token> tokens,
		int start,
		Class<?> superType
	) {
		int index = start;
		int end = index + tokens.stream()
			.skip(index)
			.map(token -> token.type() == TokenType.WHITESPACE && token.asString().contains("\n"))
			.toList()
			.indexOf(true);

		// edge case: this is the last line of the script, and there is no newline. therefore, the effect goes to the end.
		if (end == -1) end = tokens.size();

		if (end <= index) {
			// normal; there is not another effect
			return null;
		}

		List<Token> effectTokens = tokens.subList(index, Math.min(end + 1, tokens.size()));
		// This edge case shouldn't even occur
		// because the tokenizer does not output duplicate newline-containing whitespace tokens
		if (effectTokens.isEmpty()) {
			parseContext.error("Expected effect", tokens.get(index).start());
			return null;
		}

		List<TokenizedSyntax> candidates = findCandidates(parseContext, effectTokens, superType);

		if (candidates.isEmpty()) {
			parseContext.error("No statement matched", tokens.get(index).start());
			return null;
		}

		Stream<Match<SyntaxNode>> candidateNodes = candidates.stream()
			// attempt to parse each candidate
			.map(candidate -> tryParse(parseContext, candidate, tokens.subList(index, tokens.size())))
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

	private @NotNull List<Match<ExpressionNode<?>>> parseExpression(
		@NotNull ParseContext parseContext,
		@NotNull List<Token> tokens,
		int start
	) {
		int index = start;

		List<TokenizedSyntax> candidates = findCandidates(parseContext, tokens.subList(index, tokens.size()), ExpressionNodeType.class);

		if (candidates.isEmpty()) {
			parseContext.error("No expression matched", tokens.get(index).start());
			return List.of();
		}

		return (List<Match<ExpressionNode<?>>>) candidates.stream()
			.filter(candidate -> candidate.nodeType().canBeParsed(parseContext))
			.map(candidate -> tryParse(parseContext, candidate, tokens.subList(index, tokens.size())))
			.filter(Objects::nonNull)
			.map(node -> {
				if (node.node() instanceof ExpressionNode<?> exprNode)
					return new Match<ExpressionNode<?>>(exprNode, node.length());
				return null;
			})
			.filter(Objects::nonNull)
			.toList();
	}

	/**
	 * Tries to parse the given tokens which matched the given tokenized syntax to a syntax node.
	 * @param parseContext The parse stack containing the source, diagnostics, and other context on current parsing.
	 * @param tokenizedSyntax The tokenized syntax that matched the tokens.
	 * @param tokens The tokens to parse.
	 * @return The parsed syntax node, or null if failed.
	 */
	private @Nullable Match<SyntaxNode> tryParse(
		@NotNull ParseContext parseContext,
		@NotNull TokenizedSyntax tokenizedSyntax,
		@NotNull List<Token> tokens
	) {

		// push this node onto context while we work with it
		parseContext.push(tokenizedSyntax.nodeType());

		int tokenIndex = 0;
		int syntaxIndex = 0;
		List<SyntaxNode> children = new LinkedList<>();

		for (Token token : tokenizedSyntax.tokens()) {
			if (token.type() != TokenType.SYNTAX) {
				tokenIndex++;
				syntaxIndex++;
				continue;
			}

			SyntaxPatternElement element = (SyntaxPatternElement) token.value();

			// whether to push a generic frame containing the pattern's inputs
			// this is if it's not a section and if it has inputs
			// sections use their own scope system
			boolean shouldPushGenericFrame = !element.inputs().isEmpty() && !element.syntaxType().equals("section");

			if (shouldPushGenericFrame) {
				pushInputs(parseContext, element.inputs());
			}

			switch (element.syntaxType()) {
				case "token" -> {
					TokenType target = TokenType.fromName(element.output());
					if (tokens.get(tokenIndex).type() != target) {
						parseContext.error("Expected " + target + " but got " + tokens.get(tokenIndex).type(), tokens.get(tokenIndex).start());
						return null;
					}
					children.add(new TokenNode(tokens.get(tokenIndex)));
					tokenIndex++;
				}
				case "expr" -> {
					List<Match<ExpressionNode<?>>> expressionCandidates = parseExpression(parseContext, tokens, tokenIndex);
					if (expressionCandidates.isEmpty()) return null;

					final int fuckYouJava = tokenIndex;
					final int fuckYouJava2 = syntaxIndex;
					Match<ExpressionNode<?>> expression = expressionCandidates.stream()
						.filter(candidate -> {
								int nextTokenIndex = fuckYouJava + candidate.length();
								if (nextTokenIndex >= tokens.size()) return false;

								return TokenComparer.canMatch(tokenizedSyntax.tokens().subList(fuckYouJava2, tokenizedSyntax.tokens().size()), tokens.subList(nextTokenIndex, tokens.size()));
							})
						.max(Comparator.comparingInt(Match::length))
						.orElse(null);

					if (expression == null) {
						parseContext.error("No expression matched", tokens.get(tokenIndex).start());
						return null;
					}

					children.add(expression.node());
					tokenIndex += expression.length();
				}
				case "effect" -> {
					Match<StatementNode> effect = parseStatement(parseContext, tokens, tokenIndex, EffectNodeType.class);
					if (effect == null) return null;
					children.add(effect.node());
					tokenIndex += effect.length();
				}
				case "condition" -> {
					throw new UnsupportedOperationException("TODO conditions");
				}
				case "section" -> {
					String scopeName = element.inputs().size() == 1 ? element.inputs().getFirst().name() : null;
					SectionScope scope = null;
					if (scopeName != null) {
						scope = scopes.stream().filter(s -> s.name().equals(scopeName)).findFirst().orElse(null);
						if (scope == null) {
							parseContext.error("Unknown scope " + scopeName, tokens.get(tokenIndex).start());
							return null;
						}

						pushInputs(parseContext, scope.inputs());
					}
					Match<SectionNode> section = parseSection(parseContext, scope, tokens, tokenIndex);
					if (section == null) return null;
					children.add(section.node());
					tokenIndex += section.length();
					if (scope != null) parseContext.popSyntaxFrame();
				}
				case "entries" -> {
					String scopeName = element.inputs().size() == 1 ? element.inputs().getFirst().name() : null;
					SectionScope scope = null;
					if (scopeName != null) {
						scope = scopes.stream().filter(s -> s.name().equals(scopeName)).findFirst().orElse(null);
						if (scope == null) {
							parseContext.error("Unknown scope " + scopeName, tokens.get(tokenIndex).start());
							return null;
						}
					}
					Match<EntryStructureSectionNode> entries = parseEntrySection(parseContext, scope, tokens, tokenIndex);
					if (entries == null) return null;
					children.add(entries.node());
					tokenIndex += entries.length();
				}
				default -> throw new IllegalStateException("Unknown syntax type: " + element.syntaxType());
			}

			if (shouldPushGenericFrame) {
				parseContext.popSyntaxFrame();
			}

			syntaxIndex++;
		}

		parseContext.pop();

		// TODO: pass through result object to capture parse diagnostics
		return new Match<>(tokenizedSyntax.nodeType().create(children), tokenIndex);
	}

	private void pushInputs(@NotNull ParseContext parseContext, List<SyntaxPatternElement.Input> inputs) {
		parseContext.pushSyntaxFrame(inputs.stream().map(input -> {
			InputNode.Type type = new InputNode.Type(input.name(), input.type());
			var result = Tokenizer.tokenizeSyntax(new SyntaxScriptSource("input", input.name()), type);
			if (!result.isSuccess()) {
				throw new IllegalStateException("Fatal edge case: input tokenization failed");
			}
			return result.get().getFirst();
		}).toList());
	}

	/**
	 * Finds the candidates that can match the given tokens that are of the given super type.
	 * @param tokens The tokens to match against.
	 * @param superType The super type to bound candidates to.
	 * @return The candidates that can match the tokens.
	 */
	private @NotNull List<TokenizedSyntax> findCandidates(
		@NotNull ParseContext context,
		@NotNull List<Token> tokens,
		@NotNull Class<?> superType
	) {
		return context.availableSyntaxes().stream()
			.filter(tokenizedSyntax -> superType.isInstance(tokenizedSyntax.nodeType()))
			.filter(tokenizedSyntax -> tokenizedSyntax.canMatch(tokens))
			.toList();
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

				var tokens = Tokenizer.tokenizeSyntax(source, nodeType);

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
