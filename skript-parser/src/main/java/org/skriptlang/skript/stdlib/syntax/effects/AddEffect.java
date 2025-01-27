package org.skriptlang.skript.stdlib.syntax.effects;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.EffectNode;
import org.skriptlang.skript.api.nodes.EffectNodeType;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.TypeOperationUtils;

import java.util.List;

public class AddEffect implements EffectNode {
	public static final EffectNodeType<AddEffect> TYPE = new EffectNodeType<>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of(
				"(add|give) <expr> to <expr>",
				"give <expr> <expr>"
			);
		}

		@Override
		public @NotNull AddEffect create(@NotNull List<SyntaxNode> children, int matchedPattern) {
			ExpressionNode<?> receiver = matchedPattern == 0 ? (ExpressionNode<?>) children.get(1) : (ExpressionNode<?>) children.get(0);
			ExpressionNode<?> value = matchedPattern == 0 ? (ExpressionNode<?>) children.get(0) : (ExpressionNode<?>) children.get(1);

			return new AddEffect(value, receiver);
		}
	};

	private final ExpressionNode<?> valueSelector;
	private final ExpressionNode<?> receiverSelector;

	public AddEffect(ExpressionNode<?> valueSelector, ExpressionNode<?> receiverSelector) {
		this.valueSelector = valueSelector;
		this.receiverSelector = receiverSelector;
	}

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {

		return TypeOperationUtils.applyAdd(receiverSelector.resolve(context), valueSelector.resolve(context).toValue())
			? ExecuteResult.success()
			: ExecuteResult.failure(new ErrorValue("Failed to add value to receiver - does it support adding?"));

	}
}
