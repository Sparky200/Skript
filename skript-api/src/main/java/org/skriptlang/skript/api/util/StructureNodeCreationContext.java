package org.skriptlang.skript.api.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.entries.StructureEntryNode;

/**
 * An extended version of {@link NodeCreationContext} providing access to the values associated with each entry.
 */
public interface StructureNodeCreationContext extends NodeCreationContext {

	/**
	 * All entries associated with this node.
	 * This will contain only the entries that were defined, unless fallbacks were defined.
	 */
	@NotNull StructureEntryNode[] entries();

	/**
	 * Gets an entry by name.
	 * @param name The name of the entry.
	 * @return The entry with the given name.
	 */
	@Nullable StructureEntryNode entry(String name);

	/**
	 * Gets an entry by name, throwing an exception if it does not exist.
	 * This is useful considering the usually contractual nature of getting entries during node creation.
	 * It can be guaranteed that if an entry is not optional and is defined by the node type, it will exist.
	 * @param name The name of the entry.
	 * @return The entry with the given name.
	 * @throws IllegalArgumentException If the entry does not exist.
	 */
	@NotNull StructureEntryNode entryOrThrow(String name);

}
