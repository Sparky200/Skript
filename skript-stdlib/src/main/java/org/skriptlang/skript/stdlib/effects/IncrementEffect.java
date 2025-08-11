package org.skriptlang.skript.stdlib.effects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.EffectNode;
import org.skriptlang.skript.api.nodes.EffectNodeType;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.TypeOperationUtils;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.effect;

public record IncrementEffect(
	@NotNull ExpressionNode receiverSelector,
	@Nullable ExpressionNode amountSelector
) implements EffectNode {

	public static final EffectNodeType<IncrementEffect> TYPE = effect(IncrementEffect.class)
		.syntaxes("increment <expr> [by <expr>]")
		.create((children, matchedPattern) -> {
			ExpressionNode amountSelector = matchedPattern == 0 ? (ExpressionNode) children.get(1) : null;
			return new IncrementEffect((ExpressionNode) children.getFirst(), amountSelector);
		})
		.build();

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {

		return TypeOperationUtils.applyIncrement(receiverSelector.resolve(context), amountSelector == null ? null : amountSelector.resolve(context).toValue())
			? ExecuteResult.success()
			: ExecuteResult.failure(new ErrorValue("Failed to increment value - does it support incrementing?"));

	}
}
