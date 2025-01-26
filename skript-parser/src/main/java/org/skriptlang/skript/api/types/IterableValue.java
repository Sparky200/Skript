package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.skriptlang.skript.api.types.base.SkriptValueTypeFactory.skriptType;

/**
 * Marks an object that can be used in iterative syntax.
 */
public abstract class IterableValue extends SkriptValue implements Iterable<SkriptValue> {
	public static final StagedSkriptValueType<IterableValue> TYPE = skriptType("iterable", IterableValue.class)
		.build();

	/**
	 * Creates an iterator for this value.
	 */
	@Override
	public abstract @NotNull Iterator<SkriptValue> iterator();

	/**
	 * Creates a stream for this value.
	 */
	@Contract(" -> new")
	public final @NotNull Stream<SkriptValue> stream() {
		return StreamSupport.stream(spliterator(), false);
	}
}
