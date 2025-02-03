package org.skriptlang.skript.api.types;

/**
 * A union between a value and a property.
 * This is mainly only used for type bounding.
 */
public interface SkriptValueOrVariable {
	/**
	 * Converts this value or variable to a value.
	 * This just allows making sure it's the value, if that's what the using code actually wants.
	 */
	SkriptValue toValue();
}
