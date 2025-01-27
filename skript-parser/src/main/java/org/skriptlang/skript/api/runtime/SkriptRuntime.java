package org.skriptlang.skript.api.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.script.Script;
import org.skriptlang.skript.api.script.ScriptSource;
import org.skriptlang.skript.api.types.*;

/**
 * The runtime environment for Skript.
 * <p>
 * The runtime stores information that is global and immutable across any and all scripts that are executed under it.
 */
public interface SkriptRuntime {

	/**
	 * Resolves the type of the given value.
	 * {@link SkriptValue#getType(SkriptRuntime)} uses this method to resolve its type.
	 * @param value the value to resolve the type of
	 * @return the type of the value
	 */
	@NotNull SkriptValueType<?> typeOf(@NotNull SkriptValue value);

	/**
	 * Resolves a type by its name.
	 * @param name the name of the type
	 * @return the type, or null if not found
	 */
	@Nullable SkriptValueType<?> getTypeByName(@NotNull String name);

	/**
	 * Resolves a type by the class of the value.
	 * Note that the class must be <i>exact</i>,
	 * meaning subclasses will not match with some type with a superclass.
	 * @param clazz the class of the value
	 * @return the type, or null if not found
	 * @param <T> the type of the value
	 */
	<T extends SkriptValue> @Nullable SkriptValueType<T> getTypeByClass(@NotNull Class<T> clazz);

	/**
	 * Adds a type to the runtime. This method is only available before the runtime is locked.
	 * @param type the type to add
	 * @return the constructed type
	 * @param <T> the type of the value
	 */
	<T extends SkriptValue> @NotNull SkriptValueType<T> addType(@NotNull StagedSkriptValueType<T> type);

	/**
	 * Gets the global context for this runtime. This is the parent-less context that all other contexts are derived from.
	 * Below this context are the contexts for each script, which are then below the contexts for each event.
	 * @return the global context
	 */
	@NotNull ExecuteContext globalContext();

	/**
	 * Creates a variable wrapping around the given property for a certain receiver.
	 * @param property the property to wrap
	 * @param receiver the receiver to wrap the property around
	 * @return the variable
	 * @param <TReceiver> the type of the receiver
	 * @param <TValue> the type of the value
	 */
	@NotNull <TReceiver extends SkriptValue, TValue extends SkriptValue> Variable.OfProperty<TReceiver, TValue> wrapProperty(SkriptProperty<TReceiver, TValue> property, TReceiver receiver);

	/**
	 * Loads a script into the runtime.
	 * @param script the script to load
	 * @return the execute context that represents this script in this runtime, or null if a structure failed.
	 */
	@Nullable ExecuteContext load(@NotNull Script script);

	/**
	 * Unloads a script from its root node.
	 * @param script the script
	 */
	void unload(@NotNull Script script);

}
