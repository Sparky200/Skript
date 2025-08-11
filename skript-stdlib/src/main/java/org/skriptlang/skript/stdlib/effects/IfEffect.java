package org.skriptlang.skript.stdlib.effects;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.BooleanValue;
import org.skriptlang.skript.api.types.NoneValue;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.SectionUtils;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.effect;

public record IfEffect(ExpressionNode condition, SectionNode trigger) implements EffectNode {
	public static final EffectNodeType<IfEffect> TYPE = effect(IfEffect.class)
		.syntaxes("if <expr::-> boolean>:<section>")
		.create((children, matchedPattern) -> new IfEffect((ExpressionNode) children.getFirst(), (SectionNode) children.getLast()))
		.build();

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {
		context.ifContext(2);

		SkriptValue value = condition.resolve(context).toValue();
		if (value instanceof NoneValue || (value instanceof BooleanValue b && !b.jvmValue())) {
			context.ifState(false);
			return ExecuteResult.success();
		}

		context.ifState(true);
		ExecuteContext runContext = context.fork();

		return SectionUtils.executeSimple(trigger, runContext);
	}
}
