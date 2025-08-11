package org.skriptlang.skript.stdlib.effects;

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

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.effect;

public record RemoveEffect(ExpressionNode valueSelector, ExpressionNode receiverSelector) implements EffectNode {
	public static final EffectNodeType<RemoveEffect> TYPE = effect(RemoveEffect.class)
		.syntaxes("remove <expr> from <expr>")
		.create((children, matchedPattern) -> new RemoveEffect((ExpressionNode) children.getFirst(), (ExpressionNode) children.getLast()))
		.build();

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {

		return TypeOperationUtils.applyRemove(receiverSelector.resolve(context), valueSelector.resolve(context).toValue())
			? ExecuteResult.success()
			: ExecuteResult.failure(new ErrorValue("Failed to remove value from receiver - does it support removing?"));

	}
}
