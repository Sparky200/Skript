package org.skriptlang.skript.parser.pattern;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.SyntaxNodeType;
import org.skriptlang.skript.parser.TokenizedSyntax;
import org.skriptlang.skript.parser.tokens.Token;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A pattern element that is a list of tokens.
 * <p>
 * Effectively this is the low level pattern element
 * that all other elements eventually contain.
 */
public class TokensPatternElement extends PatternElement {
	private final Token[] tokens;

	public TokensPatternElement(@NotNull Token[] tokens) {
		this.tokens = tokens;
	}

	public Token[] getTokens() {
		return tokens;
	}

	@Override
	public List<TokenizedSyntax> createTokenizedSyntaxes(SyntaxNodeType<?> nodeType, List<TokenizedSyntax> existingSyntaxes) {
		return existingSyntaxes.stream().map(existingSyntax -> {
			var newArray = Arrays.copyOf(existingSyntax.tokens(), existingSyntax.tokens().length + tokens.length);
			System.arraycopy(tokens, 0, newArray, existingSyntax.tokens().length, tokens.length);
			return new TokenizedSyntax(nodeType, existingSyntax.patternIndex(), newArray);
		}).toList();
	}
}
