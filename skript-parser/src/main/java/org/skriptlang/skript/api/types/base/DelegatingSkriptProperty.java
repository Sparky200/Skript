package org.skriptlang.skript.api.types.base;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.types.SkriptValue;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A Skript property that delegates behavior to lambda functions.
 */
public final class DelegatingSkriptProperty<TReceiver extends SkriptValue, TValue extends SkriptValue> extends SkriptPropertyBase<TReceiver, TValue> {
	private final @NotNull Function<TReceiver, TValue> getter;
	private final @Nullable BiFunction<TReceiver, TValue, Boolean> setter;
	private final @Nullable BiFunction<TReceiver, SkriptValue, Boolean> adder;
	private final @Nullable BiFunction<TReceiver, SkriptValue, Boolean> remover;
	private final @Nullable BiFunction<TReceiver, SkriptValue, Boolean> incrementer;
	private final @Nullable BiFunction<TReceiver, SkriptValue, Boolean> decrementer;

	public DelegatingSkriptProperty(
		@NotNull SkriptRuntime runtime,
		@NotNull Class<TValue> valueClass,
		@NotNull Function<TReceiver, TValue> getter,
		@Nullable BiFunction<TReceiver, TValue, Boolean> setter,
		@Nullable BiFunction<TReceiver, SkriptValue, Boolean> adder,
		@Nullable BiFunction<TReceiver, SkriptValue, Boolean> remover,
		@Nullable BiFunction<TReceiver, SkriptValue, Boolean> incrementer,
		@Nullable BiFunction<TReceiver, SkriptValue, Boolean> decrementer
	) {
		super(runtime, valueClass);
		Preconditions.checkNotNull(getter);
		this.getter = getter;
		this.setter = setter;
		this.adder = adder;
		this.remover = remover;
		this.incrementer = incrementer;
		this.decrementer = decrementer;
	}

	@Override
	public @NotNull TValue get(TReceiver receiver) {
		return getter.apply(receiver);
	}

	@Override
	public boolean set(TReceiver receiver, TValue value) {
		if (setter != null) return setter.apply(receiver, value);
		return false;
	}

	@Override
	public boolean add(TReceiver receiver, SkriptValue value) {
		if (adder != null) return adder.apply(receiver, value);
		return super.add(receiver, value);
	}

	@Override
	public boolean remove(TReceiver receiver, SkriptValue value) {
		if (remover != null) return remover.apply(receiver, value);
		return super.remove(receiver, value);
	}

	@Override
	public boolean increment(TReceiver receiver) {
		if (incrementer != null) return incrementer.apply(receiver, null);
		return super.increment(receiver);
	}

	@Override
	public boolean decrement(TReceiver receiver) {
		if (decrementer != null) return decrementer.apply(receiver, null);
		return super.decrement(receiver);
	}
}
