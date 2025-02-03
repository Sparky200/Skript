package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.SkriptRuntime;

/**
 * A type describing a certain skript value.
 * <p>
 * This is akin to a {@link Class}, but in the Skript runtime.
 * <p>
 * Note that this is not constructed by addons -
 * addons create an intermediate type that contains close to the same information.
 * <p>
 * These value types will be constructed based on those intermediate types when the runtime is constructed.
 */
public interface SkriptValueType<T extends SkriptValue> {

	/**
	 * The runtime that owns this type.
	 * @return The runtime.
	 */
	@NotNull SkriptRuntime runtime();

	/**
	 * The supertype of this type. This should only be null for the root type representing {@link SkriptValue} directly.
	 */
	@Nullable SkriptValueType<?> superType();

	default @NotNull String name() {
		return runtime().getNameOfType(this);
	}

	/**
	 * Check if the value is an instance of this type.
	 * <p>
	 * This will return true if the value is an instance of any subtype of this type.
	 * @param value the value to check
	 * @return true if the value is an instance of this type
	 */
	default boolean isInstance(SkriptValue value) {
		return isSupertypeOf(value.getType(runtime()));
	}

	/**
	 * Check if this type is a subtype of the given type.
	 * @param type the type to check
	 * @return true if this type is a subtype of the given type
	 */
	boolean isSubtypeOf(SkriptValueType<?> type);

	/**
	 * Check if this type is a supertype of the given type.
	 * @param type the type to check
	 * @return true if this type is a supertype of the given type
	 */
	default boolean isSupertypeOf(SkriptValueType<?> type) {
		return type.isSubtypeOf(this);
	}

	/**
	 * Check if this type has a property with the given name.
	 * @param name the name of the property
	 * @return true if this type has a property with the given name
	 */
	boolean hasProperty(String name);

	/**
	 * Get the property with the given name.
	 * @param name the name of the property
	 * @return the property, or null if not found
	 */
	@Nullable SkriptProperty<? super T, ?> getProperty(String name);

	/**
	 * Get the JVM class that this type represents.
	 */
	@NotNull Class<T> valueClass();

}
