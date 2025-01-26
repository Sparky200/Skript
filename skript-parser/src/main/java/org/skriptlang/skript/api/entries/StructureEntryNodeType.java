package org.skriptlang.skript.api.entries;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.StatementNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;

import java.util.List;

public final class StructureEntryNodeType implements StatementNodeType<StructureEntryNode> {
	private final String name;
	private final String syntax;

	public StructureEntryNodeType(String name, String syntax) {
		this.name = name;
		this.syntax = syntax;
	}

	@Override
	public List<String> getSyntaxes() {
		return List.of(syntax);
	}

	@Override
	public @NotNull StructureEntryNode create(List<SyntaxNode> children) {
		return new StructureEntryNode(name, children);
	}

	public static StructureEntryNodeType of(EntryDefinition definition) {
		return new StructureEntryNodeType(definition.name(), definition.syntax());
	}
}
