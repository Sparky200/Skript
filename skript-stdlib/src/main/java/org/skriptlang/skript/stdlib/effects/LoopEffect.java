package org.skriptlang.skript.stdlib.effects;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.types.NumberValue;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.SectionUtils;

import java.util.List;

public class LoopEffect implements EffectNode {
	public static final EffectNodeType<LoopEffect> TYPE = new EffectNodeType<>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of(
				"loop <expr> times:<section>"
			);
		}

		@Override
		public @NotNull LoopEffect create(List<SyntaxNode> children, int matchedPattern) {
			return new LoopEffect((ExpressionNode) children.get(0), (SectionNode) children.get(1));
		}
	};

	private final ExpressionNode timesSelector;
	private final SectionNode trigger;

	public LoopEffect(ExpressionNode timesSelector, SectionNode trigger) {
		this.timesSelector = timesSelector;
		this.trigger = trigger;
	}

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {
		SkriptValue times = timesSelector.resolve(context).toValue();

		if (!(times instanceof NumberValue numberValue))
			return ExecuteResult.failure(new ErrorValue("Expected a number, but got " + times));

		for (int i = 0; i < numberValue.jvmValue(); i++) {
			ExecuteResult iterResult = SectionUtils.executeSimple(trigger, context);
			if (iterResult instanceof ExecuteResult.Failure)
				return iterResult;
		}

		return ExecuteResult.success();
	}
}
