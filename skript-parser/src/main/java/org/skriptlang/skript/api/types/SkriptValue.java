package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.SkriptRuntime;

import static org.skriptlang.skript.api.types.base.SkriptValueTypeFactory.skriptType;

/**
 * The base class for a skript value. This is akin to {@link Object} in Java.
 * The type will always be named "any".
 * <p>
 * The naming convention for using "Value" does not need to be followed
 * unless it's difficult to distinguish from a related class.
 */
public class SkriptValue implements SkriptValueOrVariable {
	public static final StagedSkriptValueType<SkriptValue> TYPE = skriptType("any", SkriptValue.class).build();

	/**
	 * Gets the type of this value in the current runtime.
	 *
	 * @param runtime the runtime that stores type information.
	 * @return the type of this value
	 */
	public final SkriptValueType<?> getType(SkriptRuntime runtime) {
		return runtime.typeOf(this);
	}

	/**
	 * Creates a new value that is the result of adding this value to another value.
	 * <p>
	 * This should <b>NOT</b> mutate this SkriptValue.
	 * For such behavior, don't override (or return null if a superclass overrides this)
	 * and instead override {@link #addDirectly(SkriptValue)}.
	 * @param other the value to add to this value
	 * @return the new value, or null (not NoneValue) if this value cannot be added to the other value.
	 */
	public @Nullable SkriptValue add(SkriptValue other) {
		return null;
	}

	/**
	 * Creates a new value that is the result of removing another value from this value.
	 * <p>
	 * This should <b>NOT</b> mutate this SkriptValue.
	 * For such behavior, don't override (or return null if a superclass overrides this)
	 * and instead override {@link #removeDirectly(SkriptValue)}.
	 * @param other the value to remove from this value
	 * @return the new value, or null (not NoneValue) if this value cannot have the other value removed from it.
	 */
	public @Nullable SkriptValue remove(SkriptValue other) {
		return null;
	}

	/**
	 * Gets a version of this value that is incremented from this one.
	 * By default, this will attempt to use {@link #add(SkriptValue)} with a value of 1.0.
	 * <p>
	 * This should <b>NOT</b> mutate this SkriptValue.
	 * For such behavior, don't override (or return null if a superclass overrides this)
	 * and instead override {@link #incrementDirectly()}.
	 * @return the incremented value, or null (not NoneValue) if this value cannot be incremented.
	 */
	public @Nullable SkriptValue increment() {
		return add(new NumberValue(1.0));
	}

	/**
	 * Gets a version of this value that is decremented from this one.
	 * By default, this will attempt to use {@link #remove(SkriptValue)} with a value of 1.0.
	 * <p>
	 * This should <b>NOT</b> mutate this SkriptValue.
	 * For such behavior, don't override (or return null if a superclass overrides this)
	 * and instead override {@link #decrementDirectly()}.
	 * @return the decremented value, or null (not NoneValue) if this value cannot be decremented.
	 */
	public @Nullable SkriptValue decrement() {
		return remove(new NumberValue(1.0));
	}

	/**
	 * A way for values to add other values to themselves directly.
	 * This is mutually exclusive with {@link #add(SkriptValue)}.
	 * If {@link #add(SkriptValue)} does not return null, this method will not be called.
	 * <p>
	 * This method is used for values that are mutable,
	 * where the property should not be set to a new value just to perform an addition.
	 * @param other the value to add to this value
	 * @return Whether the value was added.
	 */
	public boolean addDirectly(SkriptValue other) {
		return false;
	}

	/**
	 * A way for values to remove other values from themselves directly.
	 * This is mutually exclusive with {@link #remove(SkriptValue)}.
	 * If {@link #remove(SkriptValue)} does not return null, this method will not be called.
	 * <p>
	 * This method is used for values that are mutable,
	 * where the property should not be set to a new value just to perform a subtraction.
	 * @param other the value to remove from this value
	 * @return Whether the value was removed.
	 */
	public boolean removeDirectly(SkriptValue other) {
		return false;
	}

	/**
	 * A way for values to increment themselves directly.
	 * This is mutually exclusive with {@link #increment()}.
	 * If {@link #increment()} does not return null, this method will not be called.
	 * <p>
	 * This method is used for values that are mutable,
	 * where the property should not be set to a new value just to perform an incrementation.
	 * @return Whether the value was incremented.
	 */
	public boolean incrementDirectly() {
		return false;
	}

	/**
	 * A way for values to decrement themselves directly.
	 * This is mutually exclusive with {@link #decrement()}.
	 * If {@link #decrement()} does not return null, this method will not be called.
	 * <p>
	 * This method is used for values that are mutable,
	 * where the property should not be set to a new value just to perform a decrement.
	 * @return Whether the value was decremented.
	 */
	public boolean decrementDirectly() {
		return false;
	}
}
