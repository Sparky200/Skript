package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.ChangeType;
import org.skriptlang.skript.api.runtime.SkriptRuntime;

import java.util.Objects;

/**
 * A property that can belong to a {@link SkriptValue}.
 * This is stored under the value's {@link SkriptValueType}.
 * @param <TReceiver> The receiver - the type of the value that owns this property,
 *                    which should be the same as the type of the {@link SkriptValueType}.
 * @param <TValue> The type of the value of the property.
 */
public interface SkriptProperty<TReceiver extends SkriptValue, TValue extends SkriptValue> {

	/**
	 * The runtime that owns this property.
	 * @return The runtime.
	 */
	@NotNull SkriptRuntime runtime();

	/**
	 * Gets the value of this property from the given receiver.
	 * @param receiver The receiver to get the property from.
	 *                 This is the value that owns this property.
	 * @return The value of the property.
	 */
	@NotNull TValue get(TReceiver receiver);

	/**
	 * Sets the value of this property on the given receiver.
	 * @param receiver The receiver to set the property on.
	 *                 This is the value that owns this property.
	 * @param value The value to set the property to. Returning false makes the property effectively read-only.
	 */
	boolean set(TReceiver receiver, TValue value);

	/**
	 * Adds a value to the value of this property on the given receiver.
	 * <p>
	 * The default implementation is a no-op meaning this property doesn't support adding any type.
	 * @param receiver The receiver to add the value to.
	 * @param value The value to add.
	 * @return Whether the value was added.
	 */
	default boolean add(TReceiver receiver, SkriptValue value) {
		SkriptValue currentValue = get(receiver);

		SkriptValue addedValue = currentValue.add(value);
		if (addedValue != null) {
			// make sure added value is still applicable to this property
			if (!addedValue.getType(runtime()).isSubtypeOf(valueType())) return false;
			//noinspection unchecked
			return set(receiver, (TValue) addedValue);
		}

		return currentValue.addDirectly(value);
	}

	/**
	 * Removes a value from the value of this property on the given receiver.
	 * <p>
	 * The default implementation is a no-op meaning this property doesn't support removing any type.
	 * @param receiver The receiver to remove the value from.
	 * @param value The value to remove.
	 * @return Whether the value was removed.
	 */
	default boolean remove(TReceiver receiver, SkriptValue value) {
		SkriptValue currentValue = get(receiver);

		SkriptValue removedValue = currentValue.remove(value);
		if (removedValue != null) {
			// make sure removed value is still applicable to this property
			if (!removedValue.getType(runtime()).isSubtypeOf(valueType())) return false;
			//noinspection unchecked
			return set(receiver, (TValue) removedValue);
		}

		return currentValue.removeDirectly(value);
	}

	/**
	 * Increments the value of this property on the given receiver.
	 * <p>
	 * The default behavior is as follows:
	 * <ol>
	 * This will attempt to get the incremented value from the receiver using {@link SkriptValue#increment()}.
	 * If the value is null, it will attempt to increment the value directly using {@link SkriptValue#incrementDirectly()}.
	 * If that returns false, this method will return false.
	 *
	 * @param receiver The receiver to increment the value of.
	 * @return Whether the value was incremented.
	 */
	default boolean increment(TReceiver receiver) {
		SkriptValue currentValue = get(receiver);

		SkriptValue incrementedValue = currentValue.increment();
		if (incrementedValue != null) {
			// make sure incremented value is still applicable to this property
			if (!incrementedValue.getType(runtime()).isSubtypeOf(valueType())) return false;
			//noinspection unchecked
			return set(receiver, (TValue) incrementedValue);
		}

		return currentValue.incrementDirectly();
	}

	/**
	 * Decrements the value of this property on the given receiver.
	 * @param receiver The receiver to decrement the value of.
	 * @return Whether the value was decremented.
	 */
	default boolean decrement(TReceiver receiver) {
		return false;
	}


	/**
	 * Applies a change to the property using a delta value.
	 * The value could be anything.
	 * This method should <i>never</i> leave the change in a half-applied state.
	 * If the application fails, the property should be left unchanged.
	 * <p>
	 * By default, this method will attempt to apply the change
	 * using {@link #set(SkriptValue, SkriptValue)} if the change type is {@link ChangeType#SET}.
	 *
	 * @param receiver The receiver to apply the change to.
	 * @param changeType The type of change to apply.
	 * @param value The value to apply to the property. This must not be null unless the change type is unary.
	 * @return Whether the change was applied.
	 */
	default boolean change(TReceiver receiver, @NotNull ChangeType changeType, @Nullable SkriptValue value) {
		if (changeType.requiresValue() && value == null) return false;
		switch (changeType) {
			case SET -> {
				if (Objects.requireNonNull(value).getType(runtime()).isSubtypeOf(valueType())) {
					//noinspection unchecked
					return set(receiver, (TValue) value);
				}
			}
			case ADD -> {
				return add(receiver, value);
			}
			case REMOVE -> {
				return remove(receiver, value);
			}
			case INCREMENT -> {
				return increment(receiver);
			}
			case DECREMENT -> {
				return decrement(receiver);
			}
		}
		return false;
	}

	/**
	 * Gets the type of the value of the property.
	 * @return The type of the property.
	 */
	SkriptValueType<TValue> valueType();

	/**
	 * Creates a special variable that is bound to this property for the given receiver.
	 * @param receiver The receiver to bind the variable to. This must be of type TReceiver,
	 *                 but this is enforced at runtime.
	 * @return The variable.
	 * @see Variable.OfProperty
	 */
	@NotNull Variable.OfProperty<TReceiver, TValue> asVariable(SkriptValue receiver);
}
