package org.skriptlang.skript.api.types.base;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.types.SkriptProperty;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.SkriptValueType;
import org.skriptlang.skript.api.types.Variable;

public abstract class SkriptPropertyBase<TReceiver extends SkriptValue, TValue extends SkriptValue> implements SkriptProperty<TReceiver, TValue> {
	private final @NotNull SkriptRuntime runtime;
	private final @NotNull Class<TValue> valueClass;

	public SkriptPropertyBase(@NotNull SkriptRuntime runtime, @NotNull Class<TValue> valueClass) {
		this.runtime = runtime;
		this.valueClass = valueClass;
	}

	@Override
	public @NotNull SkriptRuntime runtime() {
		return runtime;
	}

	@Override
	public final SkriptValueType<TValue> valueType() {
		return runtime.getTypeByClass(valueClass);
	}

	@Override
	public Variable.@NotNull OfProperty<TReceiver, TValue> asVariable(SkriptValue receiver) {
		//noinspection unchecked
		return runtime.wrapProperty(this, (TReceiver) receiver);
	}
}
