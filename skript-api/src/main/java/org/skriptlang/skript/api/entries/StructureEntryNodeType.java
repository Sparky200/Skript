package org.skriptlang.skript.api.entries;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.StatementNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.util.NodeCreationContext;

import java.util.List;
import java.util.function.Function;

public final class StructureEntryNodeType implements StatementNodeType<StructureEntryNode> {
	private final @NotNull EntryDefinition definition;
	private final Function<NodeCreationContext, String> nameSelector;

	public StructureEntryNodeType(@NotNull EntryDefinition definition) {
		this.definition = definition;
		if (definition instanceof EntryDefinition.Structured structured) {
			final String name = structured.name();
			nameSelector = context -> name;

		} else if (definition instanceof EntryDefinition.Fallback(
			String unused, Function<NodeCreationContext, String> selector
		)) {
			nameSelector = selector;

		} else {
			throw new IllegalArgumentException("Cannot create a structure entry node from a " + definition.getClass().getSimpleName());
		}
	}

	public @NotNull EntryDefinition definition() {
		return definition;
	}

	@Override
	public List<String> getSyntaxes() {
		return List.of(definition.syntax());
	}

	@Override
	public @NotNull StructureEntryNode create(NodeCreationContext context) {
		List<SyntaxNode> children = List.of(context.children());
			children = children.subList(1, children.size());

		String name = nameSelector.apply(context);
		return new StructureEntryNode(name, children);
	}

	public static StructureEntryNodeType of(EntryDefinition definition) {
		return new StructureEntryNodeType(definition);
	}
}
