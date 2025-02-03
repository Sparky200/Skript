package org.skriptlang.skript.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.ChangeType;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.types.*;

public sealed abstract class VariableImpl implements Variable permits VariableImpl.OfProperty, VariableImpl.OfValue {

	private final @NotNull SkriptRuntime runtime;

	public VariableImpl(@NotNull SkriptRuntime runtime) {
		this.runtime = runtime;
	}

	@Override
	public SkriptRuntime runtime() {
		return runtime;
	}

	public static final class OfProperty<TReceiver extends SkriptValue, TValue extends SkriptValue> extends VariableImpl implements Variable.OfProperty<TReceiver, TValue> {
		private final @NotNull SkriptProperty<TReceiver, TValue> property;
		private final @NotNull TReceiver receiver;

		public OfProperty(@NotNull SkriptRuntime runtime, @NotNull SkriptProperty<TReceiver, TValue> property, @NotNull TReceiver receiver) {
			super(runtime);
			this.property = property;
			this.receiver = receiver;
		}

		@Override
		public boolean set(SkriptValue value) {
			if (!value.getType(runtime()).isSubtypeOf(valueType())) return false;
			//noinspection unchecked
			return property.set(receiver, (TValue) value);
		}

		@Override
		public boolean add(SkriptValue value) {
			return property.add(receiver, value);
		}

		@Override
		public boolean remove(SkriptValue value) {
			return property.remove(receiver, value);
		}

		@Override
		public boolean increment() {
			return property.increment(receiver);
		}

		@Override
		public boolean decrement() {
			return property.decrement(receiver);
		}

		@Override
		public boolean change(@NotNull ChangeType changeType, @Nullable SkriptValue value) {
			return property.change(receiver, changeType, value);
		}

		@Override
		public SkriptValueType<?> valueType() {
			return property.valueType();
		}

		@Override
		public TReceiver receiver() {
			return receiver;
		}

		@Override
		public TValue get() {
			return property.get(receiver);
		}
	}

	public static final class OfValue extends VariableImpl {
		private @NotNull SkriptValue value;

		public OfValue(@NotNull SkriptRuntime runtime, @NotNull SkriptValue initialValue) {
			super(runtime);
			this.value = initialValue;
		}

		public OfValue(@NotNull SkriptRuntime runtime) {
			this(runtime, NoneValue.get());
		}

		@Override
		public SkriptValue get() {
			return value;
		}

		@Override
		public boolean set(SkriptValue value) {
			this.value = value;
			return true;
		}

		@Override
		public SkriptValueType<?> valueType() {
			return value.getType(runtime());
		}
	}
}
