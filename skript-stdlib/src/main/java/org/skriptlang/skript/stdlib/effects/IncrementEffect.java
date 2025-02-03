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

public class IncrementEffect implements EffectNode {
	public static final EffectNodeType<IncrementEffect> TYPE = new EffectNodeType<>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of("increment <expr> [by <expr>]");
		}

		@Override
		public @NotNull IncrementEffect create(List<SyntaxNode> children, int matchedPattern) {
			ExpressionNode<?> amountSelector = children.size() == 2 ? (ExpressionNode<?>) children.get(1) : null;
			return new IncrementEffect((ExpressionNode<?>) children.getFirst(), amountSelector);
		}
	};

	private final @NotNull ExpressionNode<?> receiverSelector;
	private final @Nullable ExpressionNode<?> amountSelector;

	public IncrementEffect(@NotNull ExpressionNode<?> receiverSelector, @Nullable ExpressionNode<?> amountSelector) {
		this.receiverSelector = receiverSelector;
		this.amountSelector = amountSelector;
	}

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {

		return TypeOperationUtils.applyIncrement(receiverSelector.resolve(context), amountSelector == null ? null : amountSelector.resolve(context).toValue())
			? ExecuteResult.success()
			: ExecuteResult.failure(new ErrorValue("Failed to increment value - does it support incrementing?"));

	}
}
