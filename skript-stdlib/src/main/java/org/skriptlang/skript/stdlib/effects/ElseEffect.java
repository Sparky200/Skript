package org.skriptlang.skript.stdlib.effects;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.EffectNode;
import org.skriptlang.skript.api.nodes.EffectNodeType;
import org.skriptlang.skript.api.nodes.SectionNode;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.SectionUtils;

import java.util.List;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.effect;

public record ElseEffect(SectionNode trigger) implements EffectNode {
	public static final EffectNodeType<ElseEffect> TYPE = effect(ElseEffect.class)
		.syntaxes("else:<section>")
		.create(context -> new ElseEffect(context.section(0)))
		.build();

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {
		if (!context.ifContext())
			return ExecuteResult.failure(new ErrorValue(this, "else can only appear preceded by an if or else if statement"));

		// Do not evaluate anything if the previous if statement was valid
		if (context.ifState()) return ExecuteResult.success();

		ExecuteContext runContext = context.fork();

		return SectionUtils.executeSimple(trigger, runContext);
	}
}
