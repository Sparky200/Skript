package org.skriptlang.skript.stdlib.syntax.expressions;

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

public class ListExpression implements ExpressionNode<ListValue> {
	public static final ExpressionNodeType<ListExpression, ListValue> TYPE = new ExpressionNodeType<>() {
		@Override
		public Class<ListValue> getReturnType() {
			return ListValue.class;
		}

		@Override
		public List<String> getSyntaxes() {
			return List.of(
				"<expr>(,|[,] and) <expr>"
			);
		}

		@Override
		public @NotNull ListExpression create(List<SyntaxNode> children, int matchedPattern) {
			ExpressionNode<?> first = (ExpressionNode<?>) children.get(0);
			ExpressionNode<?> second = (ExpressionNode<?>) children.get(1);
			return new ListExpression(first, second);
		}
	};

	private final ExpressionNode<?> firstSelector;
	private final ExpressionNode<?> secondSelector;

	public ListExpression(ExpressionNode<?> firstSelector, ExpressionNode<?> secondSelector) {
		this.firstSelector = firstSelector;
		this.secondSelector = secondSelector;
	}

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
