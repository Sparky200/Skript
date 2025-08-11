package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.Nullable;

import static org.skriptlang.skript.api.types.base.SkriptTypeFactory.skriptType;

public class NumberValue extends SkriptValue {
	public static final SkriptType<NumberValue> TYPE = skriptType("number", NumberValue.class)
		.build();

	private final double value;

	public NumberValue(double value) {
		this.value = value;
	}

	@Override
	public @Nullable SkriptValue add(SkriptValue other) {
		return other instanceof NumberValue
			? new NumberValue(jvmValue() + ((NumberValue) other).jvmValue())
			: null;
	}

	@Override
	public @Nullable SkriptValue remove(SkriptValue other) {
		return other instanceof NumberValue
			? new NumberValue(jvmValue() - ((NumberValue) other).jvmValue())
			: null;
	}

	public double jvmValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
