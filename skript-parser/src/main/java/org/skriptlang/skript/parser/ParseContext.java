package org.skriptlang.skript.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.SyntaxNodeType;
import org.skriptlang.skript.api.scope.SectionScope;
import org.skriptlang.skript.api.script.ScriptSource;
import org.skriptlang.skript.api.util.ScriptDiagnostic;
import org.skriptlang.skript.api.util.ImmutableDeque;
import org.skriptlang.skript.parser.tokens.Token;
import org.skriptlang.skript.parser.tokens.TokenType;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * A stack plus context of a script being parsed.
 */
public class ParseContext {
	private final @NotNull ScriptSource source;
	private final @NotNull List<ScriptDiagnostic> diagnostics;

	private final Deque<Section> sections = new LinkedList<>();

	private final Deque<SyntaxNodeType<?>> contextStack = new LinkedList<>();

	private final Deque<SyntaxFrame> syntaxFrames = new LinkedList<>();

	public ParseContext(@NotNull ScriptSource source, @NotNull List<ScriptDiagnostic> diagnostics) {
		this.source = source;
		this.diagnostics = diagnostics;
	}

	public @NotNull ScriptSource source() {
		return source;
	}

	public void diagnostic(@NotNull ScriptDiagnostic diagnostic) {
		diagnostics.add(diagnostic);
	}

	public void info(@NotNull String message, int index) {
		diagnostics.add(ScriptDiagnostic.info(source, message, index));
	}

	public void warning(@NotNull String message, int index) {
		diagnostics.add(ScriptDiagnostic.warning(source, message, index));
	}

	public void error(@NotNull String message, int index) {
		diagnostics.add(ScriptDiagnostic.error(source, message, index));
	}

	public Section currentSection() {
		return sections.peek();
	}

	/**
	 * Returns the depth of the current section.
	 * Effectively, this is the amount of sections on the stack, minus 1.
	 * @return -1 if there are no sections on the stack.
	 *         Therefore, 0 becomes the depth of the root section once it is pushed.
	 */
	public int depth() {
		return sections.size() - 1;
	}

	/**
	 * Pushes a new section onto the stack, calculating and validating the indentation.
	 * <p>
	 * Intended to be called be parseSection.
	 * @param whitespace The whitespace character.
	 *                   This is only truly nullable when the depth is 0,
	 *                   and will be required otherwise.
	 * @param scope The scope of the section.
	 *              This will not apply a syntax frame - this should be done by the caller.
	 *              This parameter will only be stored in the section deque.
	 */
	public void pushSection(@Nullable Token whitespace, @Nullable SectionScope scope) {
		int newDepth = depth() + 1;
		int indent = 0;

		if (newDepth > 0) {
			if (whitespace == null) {
				throw new IllegalArgumentException("Whitespace is required when depth is greater than 0");
			}

			Section prevSection = sections.peek();

			// validate there is whitespace
			if (whitespace.type() != TokenType.WHITESPACE) {
				diagnostics.add(ScriptDiagnostic.error(source, "Expected whitespace", whitespace.start()));
				return;
			}

			// validate the whitespace is newline
			if (!whitespace.asString().contains("\n")) {
				diagnostics.add(ScriptDiagnostic.error(source, "Expected newline", whitespace.start()));
				return;
			}

			// find whitespace length
			indent = whitespace.asString().length() - whitespace.asString().lastIndexOf('\n') - 1;

			// validate the whitespace follows the inferred indentation in this stack

			// newDepth is fair validation that prevSection will exist
			//noinspection ConstantConditions
			int prevInterval = newDepth > 1 ? prevSection.getIndent() / (newDepth - 1) : prevSection.getIndent();
			if (prevInterval != 0 && indent % prevInterval != 0) {
				diagnostics.add(ScriptDiagnostic.error(source, "Indentation must be a multiple of " + prevInterval, whitespace.start()));
				return;
			}

		}

		sections.push(new Section(indent, scope));
	}

	/**
	 * Pops the current section off the stack.
	 * <p>
	 * Intended to be called by parseSection.
	 */
	public void popSection() {
		sections.pop();
	}

	public void push(SyntaxNodeType<?> node) {
		contextStack.push(node);
	}

	public void pop() {
		contextStack.pop();
	}

	public Deque<SyntaxNodeType<?>> currentContext() {
	    return new ImmutableDeque<>(contextStack);
	}

	public void pushSyntaxFrame(List<TokenizedSyntax> syntaxes) {
		syntaxFrames.push(new SyntaxFrame(syntaxes));
	}

	public void popSyntaxFrame() {
		syntaxFrames.pop();
	}

	public List<TokenizedSyntax> availableSyntaxes() {
		return syntaxFrames.stream().flatMap(frame -> frame.syntaxes().stream()).toList();
	}

	public static class Section {
		private final int indent;
		private final @Nullable SectionScope scope;

		public Section(int indent, @Nullable SectionScope scope) {
			this.indent = indent;
			this.scope = scope;
		}

		public int getIndent() {
			return indent;
		}

		public @Nullable SectionScope scope() {
			return scope;
		}
	}

	/**
	 * Represents a frame of parseable syntax.
	 */
	private record SyntaxFrame(List<TokenizedSyntax> syntaxes) {}
}
