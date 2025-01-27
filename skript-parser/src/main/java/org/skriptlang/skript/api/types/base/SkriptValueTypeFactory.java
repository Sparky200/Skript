package org.skriptlang.skript.api.types.base;

import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.StagedSkriptProperty;
import org.skriptlang.skript.api.types.StagedSkriptValueType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A factory class for making value types.
 * The factory will specifically construct {@link StagedSkriptValueType} instances.
 * @param <T> The {@link SkriptValue} instance that the type creates.
 */
public class SkriptValueTypeFactory<T extends SkriptValue> {

	private final String typeName;
	private String superTypeName = "any";
	private final Map<String, StagedSkriptProperty<T, ?>> properties = new LinkedHashMap<>();

	private final Class<T> tClass;

	private SkriptValueTypeFactory(String typeName, Class<T> tClass) {
		this.typeName = typeName;
		this.tClass = tClass;
	}

	public SkriptValueTypeFactory<T> superType(String superTypeName) {
		this.superTypeName = superTypeName;
		return this;
	}

	public SkriptValueTypeFactory<T> property(String name, StagedSkriptProperty<T, ?> property) {
		properties.put(name, property);
		return this;
	}

	public SkriptValueTypeFactory<T> property(String name, SkriptPropertyFactory<T, ?> property) {
		properties.put(name, property.build());
		return this;
	}

	public StagedSkriptValueType<T> build() {
		return new StagedSkriptValueType<>(tClass, typeName, superTypeName, properties);
	}

	/**
	 * Entrypoint to creating Skript value types.
	 * @param tClass the class of the value that this type creates
	 * @return a new SkriptValueTypeFactory for creating SkriptValueTypes
	 * @param <T> the SkriptValue class that the type creates
	 */
	public static <T extends SkriptValue> SkriptValueTypeFactory<T> skriptType(String typeName, Class<T> tClass) {
		return new SkriptValueTypeFactory<>(typeName, tClass);
	}

}
