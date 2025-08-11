package org.skriptlang.skript.stdlib.effects;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.BooleanValue;
import org.skriptlang.skript.api.types.NoneValue;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.SectionUtils;

import java.util.List;

public class IfEffect implements EffectNode {
	public static final EffectNodeType<IfEffect> TYPE = new EffectNodeType<>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of("if <expr::-> boolean>:<section>");
		}

		@Override
		public @NotNull IfEffect create(List<SyntaxNode> children, int matchedPattern) {
			return new IfEffect((ExpressionNode) children.getFirst(), (SectionNode) children.getLast());
		}
	};

	private final ExpressionNode condition;
	private final SectionNode trigger;

	public IfEffect(ExpressionNode condition, SectionNode trigger) {
		this.condition = condition;
		this.trigger = trigger;
	}

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
