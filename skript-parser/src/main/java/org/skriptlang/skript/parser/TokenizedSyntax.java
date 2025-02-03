package org.skriptlang.skript.parser;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.SyntaxNodeType;
import org.skriptlang.skript.parser.tokens.Token;
import org.skriptlang.skript.parser.tokens.TokenComparer;

import java.util.*;

/**
 * A tokenized syntax is a version of a syntax that has been transformed into token representations.
 *
 * @param nodeType The node type that this syntax belongs to.
 * @param tokens   The tokens that make up this syntax.
 *                 This will be matched against tokenized scripts to determine if this syntax is present.
 */
public record TokenizedSyntax(@NotNull SyntaxNodeType<?> nodeType, int patternIndex, @NotNull List<Token> tokens) {

	public TokenizedSyntax(@NotNull SyntaxNodeType<?> nodeType, int patternIndex, @NotNull List<Token> tokens) {
		Preconditions.checkNotNull(nodeType);
		Preconditions.checkNotNull(tokens);

		this.nodeType = nodeType;
		this.patternIndex = patternIndex;
		this.tokens = Collections.unmodifiableList(tokens);
	}

	/**
	 * Returns whether this tokenized syntax could match against the given script tokens.
	 * @param scriptTokens The script tokens to match against. Not the entire script, just the tokens relevant to this syntax.
	 */
	public boolean canMatch(@NotNull List<Token> scriptTokens) {
		return TokenComparer.canMatch(tokens, scriptTokens);
	}
}
