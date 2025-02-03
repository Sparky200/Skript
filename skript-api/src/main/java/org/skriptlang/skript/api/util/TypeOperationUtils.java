package org.skriptlang.skript.api.util;

import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;
import org.skriptlang.skript.api.types.Variable;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Utilities for properly interacting with types,
 * especially in the case of properly performing operations on SkriptValueOrVariables.
 */
public final class TypeOperationUtils {
	private TypeOperationUtils() {
		// no instance
	}

	/**
	 * Applies a change to the given receiver, using the given value and changer functions that are present on the type.
	 * @param receiver The receiver to apply the change to.
	 * @param value The value to apply to the receiver.
	 * @param variableChanger The variable's changer which will decide how to change the receiver.
	 * @param mutatingChanger The mutating changer which will mutate the receiver.
	 * @return Whether the change was applied.
	 */
	public static boolean applyChange(
		SkriptValueOrVariable receiver,
		SkriptValue value,
		BiFunction<Variable, SkriptValue, Boolean> variableChanger,
		BiFunction<SkriptValue, SkriptValue, Boolean> mutatingChanger
	) {
		if (receiver instanceof SkriptValue receiverAsValue) {
			// mutating changer is the only way to change this receiver
			return mutatingChanger.apply(receiverAsValue, value);
		} else if (receiver instanceof Variable receiverAsVariable) {
			return variableChanger.apply(receiverAsVariable, value);
		}
		// we don't know how to change this receiver -
		// someone probably implemented the union type when they shouldn't have
		return false;
	}

	/**
	 * Adds the given value to the receiver.
	 * @param receiver The receiver to add the value to.
	 * @param value The value to add.
	 * @return Whether the value was added.
	 */
	public static boolean applyAdd(
		SkriptValueOrVariable receiver,
		SkriptValue value
	) {
		return applyChange(receiver, value, Variable::add, SkriptValue::addDirectly);
	}

	/**
	 * Removes the given value from the receiver.
	 * @param receiver The receiver to remove the value from.
	 * @param value The value to remove.
	 * @return Whether the value was removed.
	 */
	public static boolean applyRemove(
		SkriptValueOrVariable receiver,
		SkriptValue value
	) {
		return applyChange(receiver, value, Variable::remove, SkriptValue::removeDirectly);
	}

	/**
	 * Does simple logic for applying a change to a receiver that doesn't involve a value.
	 * @param receiver The receiver to apply the change to.
	 * @param variableChanger The variable's changer which will decide how to change the receiver.
	 * @param mutatingChanger The mutating changer which will mutate the receiver.
	 * @return Whether the change was applied.
	 */
	private static boolean applyChangeNoValue(
		SkriptValueOrVariable receiver,
		Function<Variable, Boolean> variableChanger,
		Function<SkriptValue, Boolean> mutatingChanger
	) {
		if (receiver instanceof SkriptValue receiverAsValue) {
			// mutating changer is the only way to change this receiver
			return mutatingChanger.apply(receiverAsValue);
		} else if (receiver instanceof Variable receiverAsVariable) {
			return variableChanger.apply(receiverAsVariable);
		}
		// we don't know how to change this receiver -
		// someone probably implemented the union type when they shouldn't have
		return false;
	}

	/**
	 * Increments the receiver.
	 * @param receiver The receiver to increment.
	 * @return Whether the receiver was incremented.
	 */
	public static boolean applyIncrement(
		SkriptValueOrVariable receiver,
		@Nullable SkriptValue value
	) {
		if (value != null) return applyAdd(receiver, value);
		return applyChangeNoValue(receiver, Variable::increment, SkriptValue::incrementDirectly);
	}

	/**
	 * Decrements the receiver.
	 * @param receiver The receiver to decrement.
	 * @return Whether the receiver was decremented.
	 */
	public static boolean applyDecrement(
		SkriptValueOrVariable receiver,
		@Nullable SkriptValue value
	) {
		if (value != null) return applyRemove(receiver, value);
		return applyChangeNoValue(receiver, Variable::decrement, SkriptValue::decrementDirectly);
	}
}
