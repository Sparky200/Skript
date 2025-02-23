package org.skriptlang.skript.parser.pattern;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.SyntaxNodeType;
import org.skriptlang.skript.api.scope.InputDefinition;
import org.skriptlang.skript.parser.TokenizedSyntax;
import org.skriptlang.skript.parser.tokens.Token;
import org.skriptlang.skript.parser.tokens.TokenType;

import java.util.LinkedList;
import java.util.List;

/**
 * A pattern element representing a syntax element.
 */
public class SyntaxPatternElement extends PatternElement {
	private final @NotNull String syntaxType;
	private final @NotNull List<InputDefinition> inputs;
	private final @Nullable String output;

	public SyntaxPatternElement(@NotNull String syntaxType, @NotNull List<InputDefinition> inputs, @Nullable String output) {
		this.syntaxType = syntaxType;
		this.inputs = ImmutableList.copyOf(inputs);
		this.output = output;
	}

	public @NotNull String syntaxType() {
		return syntaxType;
	}

	public @NotNull List<InputDefinition> inputs() {
		return inputs;
	}

	public @Nullable String output() {
		return output;
	}

	@Override
	public List<TokenizedSyntax> createTokenizedSyntaxes(SyntaxNodeType<?> nodeType, List<TokenizedSyntax> existingSyntaxes) {
		return existingSyntaxes.stream().map(existingSyntax -> {
			var newList = new LinkedList<>(existingSyntax.tokens());
			newList.add(new Token(TokenType.SYNTAX, this, -1, 0, null));
			return new TokenizedSyntax(nodeType, existingSyntax.patternIndex(), newList);
		}).toList();
	}
}
