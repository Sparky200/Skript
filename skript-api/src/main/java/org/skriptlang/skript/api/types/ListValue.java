package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.skriptlang.skript.api.types.base.SkriptPropertyFactory.skriptProperty;
import static org.skriptlang.skript.api.types.base.SkriptValueTypeFactory.skriptType;

public class ListValue extends IterableValue {
	public static final StagedSkriptValueType<ListValue> TYPE = skriptType("list", ListValue.class)
		.superType("iterable")
		.property("length", skriptProperty(ListValue.class, NumberValue.class)
			.getter(ListValue::length)
		)
		.property("size", skriptProperty(ListValue.class, NumberValue.class)
			.getter(ListValue::length)
		)
		.build();

	private final List<SkriptValue> value = new LinkedList<>();

	@Override
	public boolean addDirectly(SkriptValue other) {
		value.add(other);
		return true;
	}

	@Override
	public boolean removeDirectly(SkriptValue other) {
		value.remove(other);
		return true;
	}

	@Override
	public @Nullable SkriptValue increment() {
		// should not try to add 1.0 by default because it would not be an expected behavior
		return null;
	}

	@Override
	public @Nullable SkriptValue decrement() {
		// see increment
		return null;
	}

	public NumberValue length() {
		return new NumberValue(value.size());
	}

	@Override
	public @NotNull Iterator<SkriptValue> iterator() {
		return value.iterator();
	}

	/**
	 * Note the return type is a view.
	 */
	public List<SkriptValue> jvmValue() {
		return Collections.unmodifiableList(value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder().append('[');
		int size = value.size();
		if (size > 0) builder.append(value.getFirst());
		else return "[]";
		for (int i = 1; i < size - 1; i++) {
			builder.append(", ").append(value.get(i));
		}
		if (size > 2) builder.append(",");
		builder.append(" and ").append(value.getLast());
		return builder.append(']').toString();
	}
}
