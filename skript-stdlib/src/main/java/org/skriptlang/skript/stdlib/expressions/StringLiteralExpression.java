package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.StringValue;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.expression;

public record StringLiteralExpression(ExpressionNode stringTokenSelector) implements ExpressionNode {

	public static final ExpressionNodeType<StringLiteralExpression> TYPE = expression(StringLiteralExpression.class)
		.syntaxes("<token::string>")
		.possibleReturnTypes(StringValue.TYPE)
		.create(context -> new StringLiteralExpression(context.string(0)))
		.build();

	@Override
	public @NotNull StringValue resolve(@NotNull ExecuteContext context) {
		return (StringValue) stringTokenSelector.resolve(context).toValue();
	}
}
