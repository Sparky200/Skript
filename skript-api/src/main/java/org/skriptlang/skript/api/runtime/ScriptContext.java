package org.skriptlang.skript.api.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.types.RuntimeSkriptType;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.StructValue;

/**
 * The root context for a script.
 */
public interface ScriptContext extends ExecuteContext, TypeContainer {

	/**
	 * Adds a type to the script context.
	 * This is primarily for the special case of structs,
	 * but addons are free to dynamically create their own types and put them in here.
	 * <p>
	 * <b>Warning to any addon using this: make sure you override {@link SkriptValue#getType(ExecuteContext)}
	 * and provide the type yourself.</b>
	 * An example way to do this is to require that your value is built with the type instance
	 * (see how {@link org.skriptlang.skript.api.types.StructValue StructValue} does this as an example).
	 * @param type the type to add
	 * @param <T> the type of the value
	 */
	<T extends SkriptValue> void addType(@NotNull RuntimeSkriptType<T> type);

	/**
	 * Removes a type from the script context.
	 * Used to clean up anything done with {@link #addType(RuntimeSkriptType)}.
	 */
	void removeType(@NotNull RuntimeSkriptType<?> type);
}
