package org.skriptlang.skript.parser.tokens;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.parser.pattern.SyntaxPatternElement;

import java.util.List;

/**
 * A single-purpose class for comparing script tokens to syntax tokens recursively.
 */
public final class TokenComparer {
	private TokenComparer() {
		// no instance
	}

	/**
	 * Returns whether the given script tokens can match the given syntax tokens.
	 * @param syntaxTokens The syntax tokens to match.
	 * @param scriptTokens The script tokens to match against. Not the entire script, just the tokens relevant to this syntax.
	 */
	public static boolean canMatch(@NotNull List<Token> syntaxTokens, @NotNull List<Token> scriptTokens) {
		if (syntaxTokens.size() > scriptTokens.size()) {
			// there's no way the script tokens will be able to match the syntax tokens
			return false;
		}

		int nextSyntaxTokenIndex = syntaxTokens.stream().map(token -> token.type() == TokenType.SYNTAX).toList().indexOf(true);

		if (nextSyntaxTokenIndex == -1) {
			// No more syntax tokens to match
			return allMatch(syntaxTokens, scriptTokens.subList(0, syntaxTokens.size()));
		}

		if (scriptTokens.size() < nextSyntaxTokenIndex) {
			// Not enough script tokens to match up to the next syntax token
			return false;
		}

		if (!allMatch(syntaxTokens.subList(0, nextSyntaxTokenIndex), scriptTokens.subList(0, nextSyntaxTokenIndex))) {
			return false;
		}

		if (nextSyntaxTokenIndex == syntaxTokens.size() - 1) {
			// The last token is a syntax token, so we can't match any more script tokens
			return true;
		}

		Token nextSyntaxToken = syntaxTokens.get(nextSyntaxTokenIndex);
		SyntaxPatternElement nextSyntax = (SyntaxPatternElement) nextSyntaxToken.value();
		Token nextScriptToken = scriptTokens.get(nextSyntaxTokenIndex);

		if (nextSyntax.syntaxType().equals("token")) {
			TokenType expectedType = TokenType.fromName(nextSyntax.output());
			if (nextScriptToken.type() != expectedType) {
				return false;
			}
		}

		for (int i = nextSyntaxTokenIndex; i < scriptTokens.size(); i++) {
			if (canMatch(syntaxTokens.subList(nextSyntaxTokenIndex + 1, syntaxTokens.size()), scriptTokens.subList(i, scriptTokens.size()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the first and second token list elements all match each other, disregarding syntax tokens.
	 * @param first The first token list.
	 * @param second The second token list.
	 * @return Whether all tokens match.
	 */
	private static boolean allMatch(@NotNull List<Token> first, @NotNull List<Token> second) {
		if (first.size() != second.size()) {
			return false;
		}

		for (int i = 0; i < first.size(); i++) {
			Token syntaxToken = first.get(i);
			Token scriptToken = second.get(i);

			if (!syntaxToken.matches(scriptToken)) {
				return false;
			}
		}

		return true;
	}
}
