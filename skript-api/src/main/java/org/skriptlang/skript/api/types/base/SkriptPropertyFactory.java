package org.skriptlang.skript.api.types.base;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.StagedSkriptProperty;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A factory for Skript properties.
 * This will use the {@link DelegatingSkriptProperty} class as an implementation.
 */
public final class SkriptPropertyFactory<TReceiver extends SkriptValue, TValue extends SkriptValue> {
	private final Class<TValue> valueClass;

	/**
	 * Nullable, but must be not null when the property is constructed.
	 */
	private @Nullable Function<TReceiver, TValue> getter;
	private @Nullable BiFunction<TReceiver, TValue, Boolean> setter;
	private @Nullable BiFunction<TReceiver, SkriptValue, Boolean> adder;
	private @Nullable BiFunction<TReceiver, SkriptValue, Boolean> remover;
	private @Nullable BiFunction<TReceiver, SkriptValue, Boolean> incrementer;
	private @Nullable BiFunction<TReceiver, SkriptValue, Boolean> decrementer;

	private SkriptPropertyFactory(Class<TValue> valueClass) {
		this.valueClass = valueClass;
	}

	/**
	 * Entrypoint to creating Skript properties.
	 * @param receiverClass the class of the receiver that this property is attached to. Unused, but required for typing.
	 * @param valueClass the class of the value that this property stores
	 * @return a new SkriptPropertyFactory for creating Skript properties
	 * @param <TReceiver> the SkriptValue instance that the property is attached to
	 * @param <TValue> the SkriptValue instance that the property stores
	 */
	public static <TReceiver extends SkriptValue, TValue extends SkriptValue> SkriptPropertyFactory<TReceiver, TValue> skriptProperty(Class<TReceiver> receiverClass, Class<TValue> valueClass) {
		return new SkriptPropertyFactory<>(valueClass);
	}

	public SkriptPropertyFactory<TReceiver, TValue> getter(Function<TReceiver, TValue> getter) {
		Preconditions.checkNotNull(getter, "getter must not be null");
		this.getter = getter;
		return this;
	}

	public SkriptPropertyFactory<TReceiver, TValue> setter(BiFunction<TReceiver, TValue, Boolean> setter) {
		this.setter = setter;
		return this;
	}

	public SkriptPropertyFactory<TReceiver, TValue> adder(BiFunction<TReceiver, SkriptValue, Boolean> adder) {
		this.adder = adder;
		return this;
	}

	public SkriptPropertyFactory<TReceiver, TValue> remover(BiFunction<TReceiver, SkriptValue, Boolean> remover) {
		this.remover = remover;
		return this;
	}

	public SkriptPropertyFactory<TReceiver, TValue> incrementer(BiFunction<TReceiver, SkriptValue, Boolean> incrementer) {
		this.incrementer = incrementer;
		return this;
	}

	public SkriptPropertyFactory<TReceiver, TValue> decrementer(BiFunction<TReceiver, SkriptValue, Boolean> decrementer) {
		this.decrementer = decrementer;
		return this;
	}

	public StagedSkriptProperty<TReceiver, TValue> build() {
		Preconditions.checkNotNull(getter, "getter must be set on properties");
		return new DelegatingStagedSkriptProperty<>(
			valueClass,
			getter,
			setter,
			adder,
			remover,
			incrementer,
			decrementer
		);
	}
}
