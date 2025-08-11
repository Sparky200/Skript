package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.types.base.SkriptPropertyBase;
import org.skriptlang.skript.api.types.base.RuntimeSkriptTypeBase;

import java.util.Map;

import static org.skriptlang.skript.api.types.base.SkriptTypeFactory.skriptType;

/**
 * Represents a {@code <none>} value.
 * <p>
 * Note that NoneValue works a little differently from other types and does not use the staged type system.
 */
public final class NoneValue extends SkriptValue {
	public static final String TYPE_NAME = "none";

	private static final NoneValue INSTANCE = new NoneValue();

	public static final SkriptType<NoneValue> TYPE = skriptType("none", NoneValue.class)
		// factory ignores supertype properties intentionally
		.runtimeFactory(
			(runtime, source, superType, properties) ->
				new RuntimeType(runtime, source)
		)
		.build();

	private NoneValue() {}

	public static NoneValue get() {
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "<none>";
	}

	/**
	 * A Skript value type representing {@code <none>}.
	 * This is a special type (and should not be used as an example)
	 * that will always propagate null upon
	 * getting a property.
	 */
	private static final class RuntimeType extends RuntimeSkriptTypeBase<NoneValue> {
		private final RuntimeSkriptProperty<NoneValue, NoneValue> property = new PropagatingProperty(runtime());

		public RuntimeType(SkriptRuntime runtime, SkriptType<NoneValue> source) {
			//noinspection unchecked
			super(source, runtime, NoneValue.class, (RuntimeSkriptType<? super NoneValue>) runtime.getTypeByName(source.superTypeName()), Map.of());
		}

		@Override
		public boolean hasProperty(String name) {
			return true;
		}

		@Override
		public @NotNull RuntimeSkriptProperty<NoneValue, ?> getProperty(String name) {
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
