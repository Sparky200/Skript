package org.skriptlang.skript.stdlib.effects;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.TypeOperationUtils;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.effect;

public record AddEffect(ExpressionNode valueSelector, ExpressionNode receiverSelector) implements EffectNode {
	public static final EffectNodeType<AddEffect> TYPE = effect(AddEffect.class)
		.syntaxes(
			"(add|give) <expr> to <expr>",
			"give <expr> <expr>"
		)
		.create(context -> {
			ExpressionNode receiver = context.matchedPattern() == 0 ? context.expression(1) : context.expression(0);
			ExpressionNode value = context.matchedPattern() == 0 ? context.expression(0) : context.expression(1);

			return new AddEffect(value, receiver);
		})
		.build();

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {

		return TypeOperationUtils.applyAdd(receiverSelector.resolve(context), valueSelector.resolve(context).toValue())
			? ExecuteResult.success()
			: ExecuteResult.failure(new ErrorValue(this, "Failed to add value to receiver - does it support adding?"));

	}
}
