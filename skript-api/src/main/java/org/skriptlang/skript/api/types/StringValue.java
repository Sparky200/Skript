package org.skriptlang.skript.api.types;

import static org.skriptlang.skript.api.types.base.SkriptPropertyFactory.skriptProperty;
import static org.skriptlang.skript.api.types.base.SkriptTypeFactory.skriptType;

/**
 * A simple Skript value that holds a string.
 */
public final class StringValue extends SkriptValue {
	public static final SkriptType<StringValue> TYPE = skriptType("string", StringValue.class)
		.property("length", skriptProperty(StringValue.class, NumberValue.class)
			.getter(StringValue::length)
		)
		.build();

	private final String value;

	public StringValue(String value) {
		this.value = value;
	}

	public NumberValue length() {
		return new NumberValue(value.length());
	}

	@Override
	public String toString() {
		return value;
	}
}
