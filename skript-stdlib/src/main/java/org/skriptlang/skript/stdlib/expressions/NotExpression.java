package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.BooleanValue;
import org.skriptlang.skript.api.types.NoneValue;
import org.skriptlang.skript.api.types.SkriptValue;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.expression;

public record NotExpression(ExpressionNode rhsSelector) implements ExpressionNode {

	public static final ExpressionNodeType<NotExpression> TYPE = expression(NotExpression.class)
		.syntaxes("not <expr::-> boolean>")
		.possibleReturnTypes(BooleanValue.TYPE)
		.create((children, matchedPattern) -> new NotExpression((ExpressionNode) children.getFirst()))
		.build();

	@Override
	public @NotNull SkriptValue resolve(@NotNull ExecuteContext context) {
		SkriptValue rhs = rhsSelector.resolve(context).toValue();
		if (rhs instanceof BooleanValue bool) return new BooleanValue(!bool.jvmValue());
		else if (rhs instanceof NoneValue) return new BooleanValue(true);
		return NoneValue.get();
	}
}
