package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ListValue;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;

import java.util.LinkedList;
import java.util.List;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.expression;

public record ListExpression(ExpressionNode firstSelector, ExpressionNode secondSelector) implements ExpressionNode {

	public static final ExpressionNodeType<ListExpression> TYPE = expression(ListExpression.class)
		.syntaxes("<expr>(,|[,] and) <expr>")
		.possibleReturnTypes(ListValue.TYPE)
		.create(context ->
			new ListExpression(context.expression(0), context.expression(1))
		)
		.build();

	@Override
	public @NotNull ListValue resolve(@NotNull ExecuteContext context) {
		SkriptValueOrVariable firstOrVar = firstSelector.resolve(context);
		SkriptValueOrVariable secondOrVar = secondSelector.resolve(context);

		List<SkriptValue> values = new LinkedList<>();

		values.add(firstOrVar.toValue());

		if (secondSelector instanceof ListExpression) {
			values.addAll(((ListValue) secondOrVar.toValue()).jvmValue());
		} else {
			values.add(secondOrVar.toValue());
		}


		ListValue list = new ListValue();
		for (SkriptValue value : values) {
			list.addDirectly(value);
		}

		return list;
	}
}
