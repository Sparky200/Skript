package org.skriptlang.skript.stdlib.syntax.effects;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.EffectNode;
import org.skriptlang.skript.api.nodes.EffectNodeType;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.*;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.stdlib.syntax.expressions.VariableExpression;

import java.util.List;

public class SetEffect implements EffectNode {
	public static final EffectNodeType<SetEffect> TYPE = new EffectNodeType<SetEffect>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of("set <expr> to <expr>");
		}

		@Override
		public @NotNull SetEffect create(List<SyntaxNode> children) {
			return new SetEffect((ExpressionNode<?>) children.getFirst(), (ExpressionNode<?>) children.get(1));
		}
	};

	private final ExpressionNode<?> receiverSelector;
	private final ExpressionNode<?> valueSelector;

	public SetEffect(ExpressionNode<?> receiverSelector, ExpressionNode<?> valueSelector) {
		this.receiverSelector = receiverSelector;
		this.valueSelector = valueSelector;
	}

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {
		SkriptValueOrVariable receiver = receiverSelector.resolve(context);
		Variable variable = null;
		if (receiver instanceof NoneValue) {
			if (receiverSelector instanceof VariableExpression varExpr) {
				// this case covers a special case
				// where a variable expression will return NoneValue because the variable is not set.
				// since it's the set expression, we just create a new variable.
				variable = context.setVariable(varExpr.name());
			} else {
				return ExecuteResult.failure(new ErrorValue("Cannot set <none> to a value"));
			}
		} else if (receiver instanceof Variable v) {
			variable = v;
		}

		if (variable == null) return ExecuteResult.failure(new ErrorValue("Cannot set a value to a non-variable"));

		SkriptValue value = valueSelector.resolve(context).toValue();

		variable.set(value);

		return ExecuteResult.success();
	}
}
