package org.skriptlang.skript.api.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.types.RuntimeSkriptType;

/**
 * Any object that holds type mappings.
 * @see SkriptRuntime
 * @see ExecuteContext
 */
public interface TypeContainer {

	/**
	 * Resolves a type by its name.
	 * @param name the name of the type
	 * @return the type, or null if not found
	 */
	@Nullable RuntimeSkriptType<?> getTypeByName(@NotNull String name);

	/**
	 * Resolves the name of a type using the runtime's type storage.
	 * @param type the type to resolve the name of
	 * @return the name of the type
	 */
	@NotNull String getNameOfType(@NotNull RuntimeSkriptType<?> type);

}
