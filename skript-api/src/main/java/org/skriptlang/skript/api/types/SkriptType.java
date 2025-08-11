package org.skriptlang.skript.api.types;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.types.base.RuntimeSkriptTypeBase;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A value type that is staged to be constructed into a {@link RuntimeSkriptType} with a {@link SkriptRuntime}.
 * This is intended to be built by {@link org.skriptlang.skript.api.types.base.SkriptTypeFactory SkriptTypeFactory}.
 */
public final class SkriptType<T extends SkriptValue> {

	private final Class<T> valueClass;
	private final String typeName;
	private final String superTypeName;
	private final Map<String, SkriptProperty<T, ?>> properties;
	private final @Nullable RuntimeSkriptTypeFactory<T> runtimeFactory;

	public SkriptType(
		Class<T> valueClass,
		String typeName,
		String superTypeName,
		Map<String, SkriptProperty<T, ?>> properties,
		@Nullable RuntimeSkriptTypeFactory<T> runtimeFactory
	) {
		this.valueClass = valueClass;
		this.typeName = typeName;
		this.superTypeName = superTypeName;
		this.properties = ImmutableMap.copyOf(properties);
		this.runtimeFactory = runtimeFactory;
	}

	public Class<T> valueClass() {
		return valueClass;
	}

	public String typeName() {
		return typeName;
	}

	public String superTypeName() {
		return superTypeName;
	}

	/**
	 * Constructs the value type using the staged information.
	 * There is a contractual guarantee that the supertype will be constructed before this type, if it exists.
	 * @param runtime The runtime to construct the type with.
	 * @return The constructed type.
	 */
	public RuntimeSkriptType<T> construct(SkriptRuntime runtime) {
		@SuppressWarnings("unchecked") RuntimeSkriptType<? super T> superType = (RuntimeSkriptType<? super T>) runtime.getTypeByName(superTypeName);
		if (superType == null && !typeName.equals("any") && !superTypeName.equals("any"))
			throw new IllegalArgumentException("Cannot construct '" + typeName
				+ "' because supertype '" + superTypeName + "' not found in runtime");

		Map<String, RuntimeSkriptProperty<? super T, ?>> constructedProperties = new LinkedHashMap<>();

		for (Map.Entry<String, SkriptProperty<T, ?>> entry : properties.entrySet()) {
			constructedProperties.put(entry.getKey(), entry.getValue().construct(runtime));
		}
		if (superType != null)
			constructedProperties.putAll(superType.properties());

		if (runtimeFactory != null && superType != null) {
			return runtimeFactory.construct(runtime, this, superType, constructedProperties);
		}
		// otherwise, use the default type base

		return new RuntimeSkriptTypeBase<>(this, runtime, valueClass, superType, constructedProperties);
	}

}
