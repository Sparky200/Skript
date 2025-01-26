package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.ChangeType;
import org.skriptlang.skript.api.runtime.SkriptRuntime;

/**
 * A variable is anything that can be changed.
 * For example, a variable could have a value.
 * It could also have a property and a receiver value.
 * <p>
 * Variables are <i>not</i> type-enforced.
 */
public interface Variable extends SkriptValueOrVariable {

	SkriptRuntime runtime();

	SkriptValue get();

	boolean set(SkriptValue value);

	default boolean add(SkriptValue value) {
		SkriptValue currentValue = get();

		SkriptValue addedValue = currentValue.add(value);

		return addedValue != null
			? set(addedValue)
			: currentValue.addDirectly(value);
	}

	default boolean remove(SkriptValue value) {
		SkriptValue currentValue = get();

		SkriptValue removedValue = currentValue.remove(value);

		return removedValue != null
			? set(removedValue)
			: currentValue.removeDirectly(value);
	}

	default boolean increment() {
		SkriptValue currentValue = get();

		SkriptValue incrementedValue = currentValue.increment();

		return incrementedValue != null
			? set(incrementedValue)
			: currentValue.incrementDirectly();
	}

	default boolean decrement() {
		SkriptValue currentValue = get();

		SkriptValue decrementedValue = currentValue.decrement();

		return decrementedValue != null
			? set(decrementedValue)
			: currentValue.decrementDirectly();
	}

	default boolean change(@NotNull ChangeType changeType, @Nullable SkriptValue value) {
		if (changeType.requiresValue() && value == null) return false;
		return switch (changeType) {
			case SET -> set(value);
			case ADD -> add(value);
			case REMOVE -> remove(value);
			case INCREMENT -> increment();
			case DECREMENT -> decrement();
		};
	}

	SkriptValueType<?> valueType();

	/**
	 * A variable that's wrapped around some {@link SkriptProperty}.
	 * This is essentially a wrapper around {@link SkriptProperty} for a specific receiver.
	 * @param <TReceiver>
	 * @param <TValue>
	 */
	interface OfProperty<TReceiver extends SkriptValue, TValue extends SkriptValue> extends Variable {

		TReceiver receiver();

		@Override
		TValue get();

	}
}
