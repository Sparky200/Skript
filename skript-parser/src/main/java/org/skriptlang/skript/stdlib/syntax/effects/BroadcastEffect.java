package org.skriptlang.skript.stdlib.syntax.effects;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.EffectNode;
import org.skriptlang.skript.api.nodes.EffectNodeType;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.util.ExecuteResult;

import java.util.List;

public class BroadcastEffect implements EffectNode {
	public static final EffectNodeType<BroadcastEffect> TYPE = new EffectNodeType<>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of("broadcast <expr>");
		}

		@Override
		public @NotNull BroadcastEffect create(List<SyntaxNode> children) {
			return new BroadcastEffect((ExpressionNode<?>) children.getFirst());
		}
	};

	private final ExpressionNode<?> valueSelector;

	public BroadcastEffect(ExpressionNode<?> valueSelector) {
		this.valueSelector = valueSelector;
	}

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {
		System.out.println("BROADCAST: " + valueSelector.resolve(context).toValue());
		return ExecuteResult.success();
	}
}
