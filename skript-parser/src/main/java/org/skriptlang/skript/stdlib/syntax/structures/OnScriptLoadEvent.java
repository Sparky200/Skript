package org.skriptlang.skript.stdlib.syntax.structures;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.entries.StructureEntryNode;
import org.skriptlang.skript.api.nodes.SectionNode;
import org.skriptlang.skript.api.nodes.StructureNode;
import org.skriptlang.skript.api.nodes.StructureNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.scope.InputDefinition;
import org.skriptlang.skript.api.scope.SectionScope;
import org.skriptlang.skript.api.types.ScriptInfoValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.Priority;
import org.skriptlang.skript.api.util.SectionUtils;
import org.skriptlang.skript.parser.pattern.SyntaxPatternElement;

import java.util.List;
import java.util.Map;

public class OnScriptLoadEvent implements StructureNode {
	public static final SectionScope SCOPE = new SectionScope("scriptloadevent", List.of(
		new InputDefinition("event-script", "script_info")
	));

	public static final StructureNodeType<OnScriptLoadEvent> TYPE = new StructureNodeType<>() {

		@Override
		public List<String> getSyntaxes() {
			return List.of("[on] script load:<section::scriptloadevent>");
		}

		@Override
		public @NotNull OnScriptLoadEvent create(List<SyntaxNode> children, int matchedPattern, @Nullable Map<String, StructureEntryNode> entries) {
			return new OnScriptLoadEvent((SectionNode) children.getFirst());
		}
	};

	private final @NotNull SectionNode section;

	public OnScriptLoadEvent(@NotNull SectionNode section) {
		Preconditions.checkNotNull(section);

		this.section = section;
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
