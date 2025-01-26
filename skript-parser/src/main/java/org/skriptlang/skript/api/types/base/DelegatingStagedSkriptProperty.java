package org.skriptlang.skript.api.types.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.types.SkriptProperty;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.StagedSkriptProperty;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A staged Skript property that delegates behavior to lambda functions.
 */
public final class DelegatingStagedSkriptProperty<TReceiver extends SkriptValue, TValue extends SkriptValue> implements StagedSkriptProperty<TReceiver, TValue> {
	private final @NotNull Class<TValue> valueClass;
	private final @NotNull Function<TReceiver, TValue> getter;
	private final @Nullable BiFunction<TReceiver, TValue, Boolean> setter;
	private final @Nullable BiFunction<TReceiver, SkriptValue, Boolean> adder;
	private final @Nullable BiFunction<TReceiver, SkriptValue, Boolean> remover;
	private final @Nullable BiFunction<TReceiver, SkriptValue, Boolean> incrementer;
	private final @Nullable BiFunction<TReceiver, SkriptValue, Boolean> decrementer;

	public DelegatingStagedSkriptProperty(
		@NotNull Class<TValue> valueClass,
		@NotNull Function<TReceiver, TValue> getter,
		@Nullable BiFunction<TReceiver, TValue, Boolean> setter,
		@Nullable BiFunction<TReceiver, SkriptValue, Boolean> adder,
		@Nullable BiFunction<TReceiver, SkriptValue, Boolean> remover,
		@Nullable BiFunction<TReceiver, SkriptValue, Boolean> incrementer,
		@Nullable BiFunction<TReceiver, SkriptValue, Boolean> decrementer
	) {
		this.valueClass = valueClass;
		this.getter = getter;
		this.setter = setter;
		this.adder = adder;
		this.remover = remover;
		this.incrementer = incrementer;
		this.decrementer = decrementer;
	}

	@Override
	public SkriptProperty<TReceiver, TValue> construct(SkriptRuntime runtime) {
		return new DelegatingSkriptProperty<>(
			runtime,
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
