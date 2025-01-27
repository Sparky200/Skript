package org.skriptlang.skript.api.types;

import com.google.common.collect.ImmutableMap;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.types.base.SkriptValueTypeBase;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A value type that is staged to be constructed into a {@link SkriptValueType} with a {@link SkriptRuntime}
 */
public final class StagedSkriptValueType<T extends SkriptValue> {
	private final Class<T> valueClass;
	private final String typeName;
	private final String superTypeName;
	private final Map<String, StagedSkriptProperty<T, ?>> properties;

	public StagedSkriptValueType(Class<T> valueClass, String typeName, String superTypeName, Map<String, StagedSkriptProperty<T, ?>> properties) {
		this.valueClass = valueClass;
		this.typeName = typeName;
		this.superTypeName = superTypeName;
		this.properties = ImmutableMap.copyOf(properties);
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
	public SkriptValueType<T> construct(SkriptRuntime runtime) {
		//noinspection unchecked
		SkriptValueType<? super T> superType = (SkriptValueType<? super T>) runtime.getTypeByName(superTypeName);

		Map<String, SkriptProperty<T, ?>> constructedProperties = new LinkedHashMap<>();
		for (Map.Entry<String, StagedSkriptProperty<T, ?>> entry : properties.entrySet()) {
			constructedProperties.put(entry.getKey(), entry.getValue().construct(runtime));
		}

		return new SkriptValueTypeBase<T>(runtime, valueClass, superType, constructedProperties);
	}

}
