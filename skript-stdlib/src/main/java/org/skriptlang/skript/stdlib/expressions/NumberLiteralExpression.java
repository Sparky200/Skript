package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.NumberValue;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.expression;

public record NumberLiteralExpression(NumberValue value) implements ExpressionNode {

	public static final ExpressionNodeType<NumberLiteralExpression> TYPE = expression(NumberLiteralExpression.class)
		.syntaxes("<token::number>")
		.possibleReturnTypes(NumberValue.TYPE)
		.create(context -> new NumberLiteralExpression(context.number(0)))
		.build();

	@Override
	public @NotNull NumberValue resolve(@NotNull ExecuteContext context) {
		return value;
	}
}
