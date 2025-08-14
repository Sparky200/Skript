package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.NoneValue;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.expression;

public record VariableExpression(String name) implements ExpressionNode {

	public static final ExpressionNodeType<VariableExpression> TYPE = expression(VariableExpression.class)
		.syntaxes("{<token::identifier>}")
		.create(context -> new VariableExpression(context.token(0).tokenContents()))
		.build();

	@Override
	public @NotNull SkriptValueOrVariable resolve(@NotNull ExecuteContext context) {
		//noinspection DataFlowIssue
		return context.hasVariable(name) ? context.getVariable(name) : NoneValue.get();
	}
}
