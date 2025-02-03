package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.NotNull;

/**
 * A basic node wrapping around a single token.
 * @param tokenContents The contents of the source token.
 */
public record TokenNode(@NotNull String tokenContents) implements SyntaxNode { }
