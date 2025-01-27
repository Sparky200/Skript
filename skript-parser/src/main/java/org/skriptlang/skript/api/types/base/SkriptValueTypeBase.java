package org.skriptlang.skript.api.types.base;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.types.SkriptProperty;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.SkriptValueType;

import java.util.Map;

/**
 * A base class for skript value types.
 * @param <T> The SkriptValue class this type represents.
 */
public class SkriptValueTypeBase<T extends SkriptValue> implements SkriptValueType<T> {
	private final @NotNull SkriptRuntime runtime;
	private final Class<T> valueClass;
	private final SkriptValueType<? super T> superType;

	private final Map<String, SkriptProperty<T, ?>> properties;

	public SkriptValueTypeBase(
		@NotNull SkriptRuntime runtime,
		@NotNull Class<T> valueClass,
		@Nullable SkriptValueType<? super T> superType,
		@NotNull Map<String, SkriptProperty<T, ?>> properties
	) {
		this.runtime = runtime;
		this.valueClass = valueClass;
		this.superType = superType;
		this.properties = ImmutableMap.copyOf(properties);
	}

	@Override
	public @NotNull SkriptRuntime runtime() {
		return runtime;
	}

	@Override
	public @Nullable SkriptValueType<?> superType() {
		return superType;
	}

	@Override
	public boolean isSubtypeOf(SkriptValueType<?> type) {
		SkriptValueType<?> current = this;
		while (current != null) {
			if (current == type) {
				return true;
			}
			current = current.superType();
		}
		return false;
	}

	@Override
	public boolean hasProperty(String name) {
		return properties.containsKey(name) || (superType != null && superType.hasProperty(name));
	}

	@Override
	public @Nullable SkriptProperty<? super T, ?> getProperty(String name) {
		if (properties.containsKey(name)) return properties.get(name);
		return superType != null ? superType.getProperty(name) : null;
	}

	@Override
	public @NotNull Class<T> valueClass() {
		return valueClass;
	}
}
