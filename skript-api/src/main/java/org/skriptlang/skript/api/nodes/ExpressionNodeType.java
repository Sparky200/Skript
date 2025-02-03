package org.skriptlang.skript.api.nodes;

import org.skriptlang.skript.api.types.SkriptValueOrVariable;

public interface ExpressionNodeType<T extends ExpressionNode<TResolveType>, TResolveType extends SkriptValueOrVariable> extends SyntaxNodeType<T> {

	/**
	 * Gets the lowest level return type that this node can return, regardless of context.
	 * This is used by the parser to perform very basic type-based filtering.
	 */
	Class<TResolveType> getReturnType();

}
