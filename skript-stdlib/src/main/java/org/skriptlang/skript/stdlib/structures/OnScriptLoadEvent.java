package org.skriptlang.skript.stdlib.structures;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.SectionNode;
import org.skriptlang.skript.api.nodes.StructureNode;
import org.skriptlang.skript.api.nodes.StructureNodeType;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.scope.InputDefinition;
import org.skriptlang.skript.api.scope.SectionScope;
import org.skriptlang.skript.api.types.ScriptInfoValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.Priority;
import org.skriptlang.skript.api.util.SectionUtils;

import java.util.List;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.structure;

public record OnScriptLoadEvent(@NotNull SectionNode section) implements StructureNode {
	public static final SectionScope SCOPE = new SectionScope("scriptloadevent", List.of(
		new InputDefinition("event-script", "script_info")
	));

	public static final StructureNodeType<OnScriptLoadEvent> TYPE = structure(OnScriptLoadEvent.class)
		.syntaxes("[on] script load:<section::scriptloadevent>")
		.create(context ->
			new OnScriptLoadEvent(context.section(0))
		)
		.build();

	public OnScriptLoadEvent {
		Preconditions.checkNotNull(section);

	}


	@Override
	public Priority priority() {
		return EVENT;
	}

	@Override
	public @NotNull ExecuteResult load(@NotNull ExecuteContext context) {
		ExecuteContext structureContext = context.fork();
		structureContext.setLiteralVariable("event-script", ScriptInfoValue.ofSource(context.script()));
		return SectionUtils.executeSimple(section, structureContext);
	}
}
