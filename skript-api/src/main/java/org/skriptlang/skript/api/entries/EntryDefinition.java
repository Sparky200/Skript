package org.skriptlang.skript.api.entries;

import org.skriptlang.skript.api.util.NodeCreationContext;

import java.util.function.Function;

public sealed interface EntryDefinition permits EntryDefinition.Structured, EntryDefinition.Fallback {
	/**
	 * The syntax that this entry in the structure must adhere to.
	 */
	String syntax();

	/**
	 * A structure entry that's directly defined as part of the structure.
	 * @param name The name of the structure entry (in code).
	 * @param syntax The syntax that this entry in the structure must adhere to.
	 * @param optional Whether this entry can be omitted when using this structure.
	 */
	record Structured(String name, String syntax, boolean optional) implements EntryDefinition {}

	/**
	 * A structure entry that's "defined in the script"
	 * or otherwise, an entry whose name could be anything until "executed".
	 * This is only possible if this definition is in the structure definition.
	 */
	record Fallback(String syntax, Function<NodeCreationContext, String> nameSelector) implements EntryDefinition {}
}
