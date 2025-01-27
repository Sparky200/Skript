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

public class RemoveEffect implements EffectNode {
	public static final EffectNodeType<RemoveEffect> TYPE = new EffectNodeType<>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of("remove <expr> from <expr>");
		}

		@Override
		public @NotNull RemoveEffect create(@NotNull List<SyntaxNode> children, int matchedPattern) {
			return new RemoveEffect(
				(ExpressionNode<?>) children.get(0),
				(ExpressionNode<?>) children.get(1)
			);
		}
	};

	private final ExpressionNode<?> valueSelector;
	private final ExpressionNode<?> receiverSelector;

	public RemoveEffect(ExpressionNode<?> valueSelector, ExpressionNode<?> receiverSelector) {
		this.valueSelector = valueSelector;
		this.receiverSelector = receiverSelector;
	}

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {

		return TypeOperationUtils.applyRemove(receiverSelector.resolve(context), valueSelector.resolve(context).toValue())
			? ExecuteResult.success()
			: ExecuteResult.failure(new ErrorValue("Failed to remove value from receiver - does it support removing?"));

	}
}
