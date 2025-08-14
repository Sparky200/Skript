package org.skriptlang.skript.stdlib.effects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.EffectNode;
import org.skriptlang.skript.api.nodes.EffectNodeType;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.TypeOperationUtils;

import java.util.List;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.effect;

public record DecrementEffect(@NotNull ExpressionNode receiverSelector,
							  @Nullable ExpressionNode amountSelector) implements EffectNode {
	public static final EffectNodeType<DecrementEffect> TYPE = effect(DecrementEffect.class)
		.syntaxes("decrement <expr> [by <expr>]")
		.create(context -> {
			ExpressionNode amountSelector = context.matchedPattern() == 0 ? context.expression(1) : null;
			return new DecrementEffect(context.expression(0), amountSelector);
		})
		.build();

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {

		return TypeOperationUtils.applyDecrement(
			receiverSelector.resolve(context),
			amountSelector == null ? null : amountSelector.resolve(context).toValue()
		)
			? ExecuteResult.success()
			: ExecuteResult.failure(new ErrorValue(this, "Failed to decrement value - does it support decrementing?"));

	}
}
