package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.parser.tokens.Token;

/**
 * A basic node wrapping around a single token.
 */
public record TokenNode(@NotNull Token token) implements SyntaxNode { }
