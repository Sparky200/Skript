package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.NoneValue;

import java.util.List;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.expression;

public class NoneLiteralExpression implements ExpressionNode {

	public static final ExpressionNodeType<NoneLiteralExpression> TYPE = expression(NoneLiteralExpression.class)
		.syntaxes("none")
		.possibleReturnTypes(NoneValue.TYPE)
		.create(context -> new NoneLiteralExpression())
		.build();

	@Override
	@NotNull
	public NoneValue resolve(@NotNull ExecuteContext context) {
		return NoneValue.get();
	}
}
