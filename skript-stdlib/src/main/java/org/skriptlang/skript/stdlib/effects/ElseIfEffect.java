package org.skriptlang.skript.stdlib.effects;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.BooleanValue;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.types.NoneValue;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.SectionUtils;

import java.util.List;

public class ElseIfEffect implements EffectNode {
	public static final EffectNodeType<ElseIfEffect> TYPE = new EffectNodeType<>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of("else if <expr::-> boolean>:<section>");
		}

		@Override
		public @NotNull ElseIfEffect create(List<SyntaxNode> children, int matchedPattern) {
			return new ElseIfEffect((ExpressionNode<?>) children.getFirst(), (SectionNode) children.getLast());
		}
	};

	private final ExpressionNode<?> condition;
	private final SectionNode trigger;

	public ElseIfEffect(ExpressionNode<?> condition, SectionNode trigger) {
		this.condition = condition;
		this.trigger = trigger;
	}

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {
		if (!context.ifContext())
			return ExecuteResult.failure(new ErrorValue("else if can only appear preceded by an if statement"));
		// propagate
		context.ifContext(2);

		// Do not evaluate anything if the previous if statement was valid
		if (context.ifState()) return ExecuteResult.success();

		SkriptValue value = condition.resolve(context).toValue();
		if (value instanceof NoneValue || value instanceof ErrorValue || (value instanceof BooleanValue b && !b.jvmValue())) {
			context.ifState(false);
			return ExecuteResult.success();
		}

		context.ifState(true);
		ExecuteContext runContext = context.fork();

		return SectionUtils.executeSimple(trigger, runContext);
	}
}
