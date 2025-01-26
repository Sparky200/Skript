package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.types.base.SkriptPropertyBase;
import org.skriptlang.skript.api.types.base.SkriptValueTypeBase;

import java.util.Map;

/**
 * Represents a {@code <none>} value.
 */
public final class NoneValue extends SkriptValue {

	private static final NoneValue INSTANCE = new NoneValue();

	private NoneValue() {}

	public static NoneValue get() {
		return INSTANCE;
	}

	/**
	 * A Skript value type representing {@code <none>}.
	 */
	public static final class Type extends SkriptValueTypeBase<NoneValue> {
		private final SkriptProperty<NoneValue, NoneValue> property = new PropagatingProperty(runtime());

		public Type(SkriptRuntime runtime) {
			super(runtime, null, Map.of());
		}

		@Override
		public boolean hasProperty(String name) {
			return true;
		}

		@Override
		public @NotNull SkriptProperty<NoneValue, ?> getProperty(String name) {
			return property;
		}

		private static final class PropagatingProperty extends SkriptPropertyBase<NoneValue, NoneValue> {

			public PropagatingProperty(SkriptRuntime runtime) {
				super(runtime, NoneValue.class);
			}

			@Override
			public @NotNull NoneValue get(NoneValue receiver) {
				// propagate value
				return receiver;
			}

			@Override
			public boolean set(NoneValue noneValue, NoneValue value) {
				return false;
			}
		}
	}
}
