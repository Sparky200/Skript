package org.skriptlang.skript.api.types;

import org.skriptlang.skript.api.runtime.SkriptRuntime;

/**
 * A type property that is staged to be constructed into a {@link SkriptProperty} with a {@link SkriptRuntime}.
 * Since there is a lot of opportunity for extension of SkriptProperty, this is an abstraction.
 */
public interface StagedSkriptProperty<TReceiver extends SkriptValue, TValue extends SkriptValue> {

	/**
	 * Constructs the type property using the staged information.
	 * @param runtime The runtime to construct the type with.
	 * @return The constructed type.
	 */
	SkriptProperty<TReceiver, TValue> construct(SkriptRuntime runtime);

}
