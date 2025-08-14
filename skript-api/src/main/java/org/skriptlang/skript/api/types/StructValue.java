package org.skriptlang.skript.api.types;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.script.Script;
import org.skriptlang.skript.api.types.base.RuntimeSkriptTypeBase;
import org.skriptlang.skript.api.types.base.SkriptPropertyBase;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A special kind of value that is created by language syntax.
 * @apiNote Instances of structs are tied into their source runtime.
 *          It is very important that struct values do not jump between runtimes without manual adaptation.
 */
public final class StructValue extends SkriptValue {

	private final Map<RuntimeSkriptProperty<?, ?>, SkriptValue> properties;

	private final StructType type;

	public StructValue(StructType type) {
		this.type = type;
		properties = new LinkedHashMap<>();
	}

	@Override
	public StructType getType(ExecuteContext context) {
		return type;
	}

	public @NotNull SkriptValue getValueOf(@NotNull RuntimeSkriptProperty<?, ?> property) {
		return properties.getOrDefault(property, NoneValue.get());
	}

	public void setValueOf(@NotNull RuntimeSkriptProperty<?, ?> property, @NotNull SkriptValue value) {
		Preconditions.checkNotNull(property, "property cannot be null");
		properties.put(property, value);
	}

	public static final class StructType extends RuntimeSkriptTypeBase<StructValue> {
		private final @NotNull Script sourceScript;

		public StructType(
			@NotNull SkriptRuntime runtime,
			@NotNull Script sourceScript,
			@NotNull SkriptType<StructValue> source,
			@NotNull Map<String, RuntimeSkriptProperty<? super StructValue, ?>> properties
		) {
			super(source, runtime, source.valueClass(), runtime.getTypeByClass(SkriptValue.class), properties);
			this.sourceScript = sourceScript;
		}

		public @NotNull Script sourceScript() {
			return sourceScript;
		}

		@Override
		public @NotNull String name() {
			return source().typeName();
		}

		public static final class Property extends SkriptPropertyBase<StructValue, SkriptValue> {
			public Property(@NotNull SkriptRuntime runtime, @NotNull Class<? extends SkriptValue> valueClass) {
				//noinspection unchecked
				super(runtime, (Class<SkriptValue>) valueClass);
			}

			@Override
			public @NotNull SkriptValue get(StructValue structValue) {
				return structValue.getValueOf(this);
			}

			@Override
			public boolean set(StructValue structValue, SkriptValue value) {
				structValue.setValueOf(this, value);
				return true;
			}
		}
	}
}
