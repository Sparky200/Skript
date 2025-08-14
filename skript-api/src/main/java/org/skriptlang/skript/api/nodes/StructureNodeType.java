package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.entries.EntryStructureDefinition;
import org.skriptlang.skript.api.entries.StructureEntryNode;
import org.skriptlang.skript.api.entries.StructureSectionNode;
import org.skriptlang.skript.api.util.NodeCreationContext;
import org.skriptlang.skript.api.util.StructureNodeCreationContext;

import java.util.List;
import java.util.Map;

public abstract class StructureNodeType<T extends StructureNode> implements StatementNodeType<T> {

	public @Nullable EntryStructureDefinition structure() {
		return null;
	}

	@Override
	@Contract(value = "_ -> new", pure = true)
	public final @NotNull T create(NodeCreationContext context) {
		if (context instanceof StructureNodeCreationContext structureContext) {
			return create(structureContext);
		}
		throw new IllegalArgumentException("Cannot create a structure node from a " + context.getClass().getSimpleName());
	}

	/**
	 * Creates a new structure node.
	 * @param context The creation context containing children, pattern index, and entries.
	 * @return the new node.
	 */
	protected abstract @NotNull T create(StructureNodeCreationContext context);
}
