package org.skriptlang.skript.stdlib.syntax.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.entries.StructureEntryNode;
import org.skriptlang.skript.api.nodes.StructureNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;

import java.util.List;
import java.util.Map;

public class EventStructureType<T extends EventStructure> extends StructureNodeType<T> {
	private final String eventSyntax;

	public EventStructureType(String eventSyntax) {
		this.eventSyntax = eventSyntax;
	}



	@Override
	public List<String> getSyntaxes() {
		return List.of("[on] " + eventSyntax + ":<section>");
	}

	@Override
	protected @NotNull T create(@NotNull List<SyntaxNode> children, @Nullable Map<String, StructureEntryNode> entries) {
		return null;
	}
}
