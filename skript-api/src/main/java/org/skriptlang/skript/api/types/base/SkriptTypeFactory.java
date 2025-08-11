package org.skriptlang.skript.api.types.base;

import org.skriptlang.skript.api.types.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A factory class for making value types.
 * The factory will specifically construct {@link SkriptType} instances.
 * @param <T> The {@link SkriptValue} instance that the type creates.
 */
public class SkriptTypeFactory<T extends SkriptValue> {

	private final String typeName;
	private String superTypeName = "any";
	private final Map<String, SkriptProperty<T, ?>> properties = new LinkedHashMap<>();

	private final Class<T> tClass;

	private RuntimeSkriptTypeFactory<T> runtimeFactory;

	private SkriptTypeFactory(String typeName, Class<T> tClass) {
		this.typeName = typeName;
		this.tClass = tClass;
	}

	/**
	 * Sets the supertype of this type.
	 * This must resolve to an actual type upon adding to the runtime.
	 * This means the supertype must be constructed and registered first.
	 * @param superTypeName The name of the supertype, best grabbed from the supertype itself.
	 * @return This, for chaining.
	 */
	public SkriptTypeFactory<T> extend(String superTypeName) {
		this.superTypeName = superTypeName;
		return this;
	}

	/**
	 * Sets the supertype of this type directly using the staged type.
	 * This is an alias to using {@link #extend(String)} directly,
	 * and has no additional side effects from referencing the staged type directly.
	 * It should <b>NOT</b> be assumed that the supertype at runtime will actually be constructed from the type referenced here.
	 * @param type The supertype.
	 * @return This, for chaining.
	 * @see #extend(String)
	 */
	public SkriptTypeFactory<T> extend(SkriptType<? super T> type) {
		return extend(type.typeName());
	}

	/**
	 * Adds a property to the type.
	 * @param name The name of the property on this type. This <i>is</i> the syntax that should be used to grab the type.
	 * @param property The property.
	 * @return This, for chaining.
	 */
	public SkriptTypeFactory<T> property(String name, SkriptProperty<T, ?> property) {
		properties.put(name, property);
		return this;
	}

	/**
	 * Adds a property to the type directly from a factory.
	 * This is equivalent to calling {@link #property(String, SkriptProperty)} after building the factory.
	 * @param name The name of the property on this type. This <i>is</i> the syntax that should be used to grab the type.
	 * @param property The factory that creates the property.
	 * @return This, for chaining.
	 */
	public SkriptTypeFactory<T> property(String name, SkriptPropertyFactory<T, ?> property) {
		properties.put(name, property.build());
		return this;
	}

	/**
	 * Defines how to build the runtime version of this type
	 * (the version of this type that is interacted with and available at runtime).
	 * <p>
	 * This is an <i>advanced</i> use case that has internal use,
	 * but likely is not applicable to most cases.
	 * @param runtimeFactory The factory that will construct the runtime type.
	 * @return This, for chaining.
	 */
	public SkriptTypeFactory<T> runtimeFactory(RuntimeSkriptTypeFactory<T> runtimeFactory) {
		this.runtimeFactory = runtimeFactory;
		return this;
	}

	public SkriptType<T> build() {
		return new SkriptType<>(tClass, typeName, superTypeName, properties, runtimeFactory);
	}

	/**
	 * Entrypoint to creating Skript value types.
	 * @param tClass the class of the value that this type creates
	 * @return a new SkriptValueTypeFactory for creating SkriptValueTypes
	 * @param <T> the SkriptValue class that the type creates
	 */
	public static <T extends SkriptValue> SkriptTypeFactory<T> skriptType(String typeName, Class<T> tClass) {
		return new SkriptTypeFactory<>(typeName, tClass);
	}

}
