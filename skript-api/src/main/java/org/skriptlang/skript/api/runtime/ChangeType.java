package org.skriptlang.skript.api.runtime;

import org.skriptlang.skript.api.types.SkriptProperty;
import org.skriptlang.skript.api.types.SkriptValue;

/**
 * An enum used for dynamically changing the type of a value.
 * This is used with {@link SkriptProperty#change(SkriptValue, ChangeType, SkriptValue)}.
 */
public enum ChangeType {
	/**
	 * Set the value of the property to the provided value.
	 */
	SET(true),

	/**
	 * Add the provided value to the property.
	 */
	ADD(true),

	/**
	 * Remove the provided value from the property.
	 */
	REMOVE(true),

	/**
	 * Increment the property by the provided value.
	 */
	INCREMENT(false),

	/**
	 * Decrement the property by the provided value.
	 */
	DECREMENT(false),

	;

	private final boolean requiresValue;

	ChangeType(boolean requiresValue) {
		this.requiresValue = requiresValue;
	}

	public boolean requiresValue() {
		return requiresValue;
	}
}
