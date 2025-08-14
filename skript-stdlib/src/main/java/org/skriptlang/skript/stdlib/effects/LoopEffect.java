package org.skriptlang.skript.stdlib.effects;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.types.NumberValue;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.SectionUtils;

import java.util.List;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.effect;

public record LoopEffect(ExpressionNode timesSelector, SectionNode trigger) implements EffectNode {
	public static final EffectNodeType<LoopEffect> TYPE = effect(LoopEffect.class)
		.syntaxes("loop <expr> times:<section>")
		.create(context -> new LoopEffect(context.expression(0), context.section(1)))
		.build();

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {
		SkriptValue times = timesSelector.resolve(context).toValue();

		if (!(times instanceof NumberValue numberValue))
			return ExecuteResult.failure(new ErrorValue(this, "Expected a number, but got " + times));

		for (int i = 0; i < numberValue.jvmValue(); i++) {
			ExecuteResult iterResult = SectionUtils.executeSimple(trigger, context);
			if (iterResult instanceof ExecuteResult.Failure)
				return iterResult;
		}

		return ExecuteResult.success();
	}
}
