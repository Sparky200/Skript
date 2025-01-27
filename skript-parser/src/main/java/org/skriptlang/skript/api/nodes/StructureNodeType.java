package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.entries.EntryStructureDefinition;
import org.skriptlang.skript.api.entries.StructureEntryNode;
import org.skriptlang.skript.api.entries.EntryStructureSectionNode;

import java.util.List;
import java.util.Map;

public abstract class StructureNodeType<T extends StructureNode> implements StatementNodeType<T> {

	public @Nullable EntryStructureDefinition structure() {
		return null;
	}

	@Override
	@Contract(value = "_, _ -> new", pure = true)
	public final @NotNull T create(@NotNull List<SyntaxNode> children, int matchedPattern) {
		SyntaxNode last = children.getLast();
		if (last instanceof EntryStructureSectionNode(Map<String, StructureEntryNode> entries)) {
			return create(children, matchedPattern, entries);
		}
		return create(children, matchedPattern, null);
	}

	/**
	 * Creates a new structure node.
	 * @param children the children of the node, where the last child is either a section or an entry section.
	 * @param entries the entries in the node, if this node uses entries.
	 * @return the new node.
	 */
	protected abstract @NotNull T create(@NotNull List<SyntaxNode> children, int matchedPattern, @Nullable Map<String, StructureEntryNode> entries);
}
