package org.skriptlang.skript.api.types;

import static org.skriptlang.skript.api.types.base.SkriptTypeFactory.skriptType;

public class BooleanValue extends SkriptValue {
	public static final SkriptType<BooleanValue> TYPE = skriptType("boolean", BooleanValue.class)
		.build();

	private final boolean value;

	public BooleanValue(boolean value) {
		this.value = value;
	}

	public boolean jvmValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
