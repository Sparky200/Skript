package org.skriptlang.skript.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.SectionNode;
import org.skriptlang.skript.api.nodes.StatementNode;
import org.skriptlang.skript.api.nodes.StructureNode;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.script.Script;
import org.skriptlang.skript.api.types.*;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.SectionUtils;
import org.skriptlang.skript.parser.LockAccess;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SkriptRuntimeImpl implements SkriptRuntime {
	private final LockAccess lockAccess;

	private final @NotNull ExecuteContext globalContext = new ExecuteContextImpl(this, null);

	private final Map<String, SkriptValueType<?>> typesByName = new LinkedHashMap<>();
	private final Map<Class<?>, SkriptValueType<?>> typesByClass = new LinkedHashMap<>();

	private final Set<Script> loadingScripts = new LinkedHashSet<>();
	private final Map<Script, ExecuteContext> loadedScripts = new ConcurrentHashMap<>();

	public SkriptRuntimeImpl(LockAccess lockAccess) {
		this.lockAccess = lockAccess;
		addConstructedType(SkriptValue.TYPE.typeName(), SkriptValue.TYPE.construct(this));
		addConstructedType(NoneValue.TYPE_NAME, NoneValue.TYPE.construct(this));
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

	private void addConstructedType(String typeName, @NotNull SkriptValueType<?> type) {
		if (typesByName.containsKey(typeName)) throw new IllegalArgumentException("Type with name " + typeName + " already exists");
		typesByName.put(typeName, type);
		typesByClass.put(type.valueClass(), type);
	}

	@Override
	public @NotNull ExecuteContext globalContext() {
		return globalContext;
	}

	@Override
	public <TReceiver extends SkriptValue, TValue extends SkriptValue> Variable.@NotNull OfProperty<TReceiver, TValue> wrapProperty(SkriptProperty<TReceiver, TValue> property, TReceiver receiver) {
		return new VariableImpl.OfProperty<>(this, property, receiver);
	}

	@Override
	public @Nullable ExecuteContext load(@NotNull Script script) {
		synchronized (loadingScripts) {
			if (loadingScripts.contains(script)) throw new IllegalStateException("Script is already being loaded");
			if (loadedScripts.containsKey(script)) throw new IllegalStateException("Script is already loaded");
			loadingScripts.add(script);
		}

		ExecuteContext scriptContext = new ScriptContext(this, script, globalContext());

		ExecuteResult result = SectionUtils.loadStructuresIn(script.root(), scriptContext);
		if (result instanceof ExecuteResult.Failure) return null;

		synchronized (loadingScripts) {
			loadedScripts.put(script, scriptContext);
			loadingScripts.remove(script);
		}

		return scriptContext;
	}

	@Override
	public void unload(@NotNull Script script) {
		synchronized (loadingScripts) {
			if (loadingScripts.contains(script)) throw new IllegalStateException("Script is being loaded");
			if (!loadedScripts.containsKey(script)) throw new IllegalStateException("Script is not loaded");
		}

		ExecuteContext scriptContext = loadedScripts.get(script);
		SectionUtils.unloadStructuresIn(script.root(), scriptContext);

		synchronized (loadingScripts) {
			loadedScripts.remove(script);
		}
	}
}
