package org.skriptlang.skript.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.types.*;
import org.skriptlang.skript.parser.LockAccess;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SkriptRuntimeImpl implements SkriptRuntime {
	private final LockAccess lockAccess;

	private final @NotNull ExecuteContext globalContext = new ExecuteContextImpl(this, null);

	private final Map<String, SkriptValueType<?>> typesByName = new LinkedHashMap<>();
	private final Map<Class<?>, SkriptValueType<?>> typesByClass = new LinkedHashMap<>();

	public SkriptRuntimeImpl(LockAccess lockAccess) {
		this.lockAccess = lockAccess;
	}

	/**
	 * @implNote values must have a type, otherwise addons are doing something they shouldn't be doing
	 */
	@Override
	public @NotNull SkriptValueType<?> typeOf(@NotNull SkriptValue value) {
		return Objects.requireNonNull(getTypeByClass(value.getClass()));
	}

	@Override
	public @Nullable SkriptValueType<?> getTypeByName(@NotNull String name) {
		return typesByName.get(name);
	}

	@Override
	public @Nullable <T extends SkriptValue> SkriptValueType<T> getTypeByClass(@NotNull Class<T> clazz) {
		SkriptValueType<?> type = typesByClass.get(clazz);
		//noinspection unchecked
		return type == null ? null : (SkriptValueType<T>) type;
	}

	@Override
	public <T extends SkriptValue> @NotNull SkriptValueType<T> addType(@NotNull StagedSkriptValueType<T> type) {
		if (lockAccess.isLocked()) throw new IllegalStateException("Cannot add type after runtime is locked");
		if (typesByName.containsKey(type.typeName())) throw new IllegalArgumentException("Type with name " + type.typeName() + " already exists");
		if (!typesByName.containsKey(type.superTypeName())) throw new IllegalArgumentException("Super type with name " + type.superTypeName() + " does not exist");
		SkriptValueType<T> constructedType = type.construct(this);
		typesByName.put(type.typeName(), constructedType);
		typesByClass.put(type.valueClass(), constructedType);
		return constructedType;
	}

	@Override
	public @NotNull ExecuteContext globalContext() {
		return globalContext;
	}

	@Override
	public <TReceiver extends SkriptValue, TValue extends SkriptValue> Variable.@NotNull OfProperty<TReceiver, TValue> wrapProperty(SkriptProperty<TReceiver, TValue> property, TReceiver receiver) {
		return new VariableImpl.OfProperty<>(this, property, receiver);
	}
}
