package org.skriptlang.skript.api.types.base;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.types.RuntimeSkriptProperty;
import org.skriptlang.skript.api.types.SkriptType;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.RuntimeSkriptType;

import java.util.Map;

/**
 * A base class for skript value types.
 * @param <T> The SkriptValue class this type represents.
 */
public class RuntimeSkriptTypeBase<T extends SkriptValue> implements RuntimeSkriptType<T> {
	private final @NotNull SkriptType<T> source;
	private final @NotNull SkriptRuntime runtime;
	private final Class<T> valueClass;
	private final RuntimeSkriptType<? super T> superType;

	private final Map<String, RuntimeSkriptProperty<? super T, ?>> properties;

	public RuntimeSkriptTypeBase(
		@NotNull SkriptType<T> source,
		@NotNull SkriptRuntime runtime,
		@NotNull Class<T> valueClass,
		@Nullable RuntimeSkriptType<? super T> superType,
		@NotNull Map<String, RuntimeSkriptProperty<? super T, ?>> properties
	) {
		this.source = source;
		this.runtime = runtime;
		this.valueClass = valueClass;
		this.superType = superType;
		this.properties = ImmutableMap.copyOf(properties);
	}

	@Override
	public @NotNull SkriptType<T> source() {
		return source;
	}

	@Override
	public @NotNull SkriptRuntime runtime() {
		return runtime;
	}

	@Override
	public @Nullable RuntimeSkriptType<?> superType() {
		return superType;
	}

	@Override
	public boolean isSubtypeOf(RuntimeSkriptType<?> type) {
		RuntimeSkriptType<?> current = this;
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
	public @Nullable RuntimeSkriptProperty<? super T, ?> getProperty(String name) {
		if (properties.containsKey(name)) return properties.get(name);
		return superType != null ? superType.getProperty(name) : null;
	}

	@Override
	public @NotNull Map<String, RuntimeSkriptProperty<? super T, ?>> properties() {
		return properties;
	}

	@Override
	public @NotNull Class<T> valueClass() {
		return valueClass;
	}
}
