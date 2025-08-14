package org.skriptlang.skript.stdlib.effects;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.EffectNode;
import org.skriptlang.skript.api.nodes.EffectNodeType;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.util.ExecuteResult;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.effect;

public record BroadcastEffect(ExpressionNode valueSelector) implements EffectNode {
	public static final EffectNodeType<BroadcastEffect> TYPE = effect(BroadcastEffect.class)
		.syntaxes("broadcast <expr>")
		.create(context -> new BroadcastEffect(context.expression(0)))
		.build();

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {
		System.out.println("BROADCAST: " + valueSelector.resolve(context).toValue());
		return ExecuteResult.success();
	}
}
