package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.BooleanValue;

import java.util.List;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.expression;

public record BooleanLiteralExpression(boolean value) implements ExpressionNode {

	public static final ExpressionNodeType<BooleanLiteralExpression> TYPE = expression(BooleanLiteralExpression.class)
		.syntaxes("true", "false")
		.possibleReturnTypes(BooleanValue.TYPE)
		.create(context ->
			new BooleanLiteralExpression(context.matchedPattern() == 0)
		)
		.build();

	@Override
	public @NotNull BooleanValue resolve(@NotNull ExecuteContext context) {
		return new BooleanValue(value);
	}
}
