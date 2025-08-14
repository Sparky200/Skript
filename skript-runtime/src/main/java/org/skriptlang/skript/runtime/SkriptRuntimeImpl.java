package org.skriptlang.skript.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.runtime.ScriptContext;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.script.Script;
import org.skriptlang.skript.api.types.*;
import org.skriptlang.skript.api.util.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SkriptRuntimeImpl implements SkriptRuntime {
	private final LockAccess lockAccess;

	private final @NotNull ExecuteContext globalContext = new ExecuteContextImpl(this, null);

	private final Map<String, RuntimeSkriptType<?>> typesByName = new LinkedHashMap<>();
	private final Map<Class<?>, RuntimeSkriptType<?>> typesByClass = new LinkedHashMap<>();

	private final Set<Script> loadingScripts = new LinkedHashSet<>();
	private final Map<Script, ExecuteContext> loadedScripts = new ConcurrentHashMap<>();

	public SkriptRuntimeImpl(LockAccess lockAccess) {
		this.lockAccess = lockAccess;
		addConstructedType(SkriptValue.TYPE.typeName(), SkriptValue.TYPE.construct(this));
		addConstructedType(NoneValue.TYPE.typeName(), NoneValue.TYPE.construct(this));
		addType(BooleanValue.TYPE);
	}

	/**
	 * @implNote values must have a type, otherwise addons are doing something they shouldn't be doing
	 */
	@Override
	public @NotNull RuntimeSkriptType<?> typeOf(@NotNull SkriptValue value) {
		return Objects.requireNonNull(getTypeByClass(value.getClass()));
	}

	@Override
	public @Nullable RuntimeSkriptType<?> getTypeByName(@NotNull String name) {
		return typesByName.get(name);
	}

	@Override
	public @Nullable <T extends SkriptValue> RuntimeSkriptType<T> getTypeByClass(@NotNull Class<T> clazz) {
		RuntimeSkriptType<?> type = typesByClass.get(clazz);
		//noinspection unchecked
		return type == null ? null : (RuntimeSkriptType<T>) type;
	}

	@Override
	public @NotNull String getNameOfType(@NotNull RuntimeSkriptType<?> type) {
		return typesByName.entrySet().stream()
			.filter(entry -> entry.getValue() == type)
			.findFirst()
			.orElseThrow()
			.getKey();
	}

	@Override
	public <T extends SkriptValue> @NotNull RuntimeSkriptType<T> addType(@NotNull SkriptType<T> type) {
		if (lockAccess.isLocked()) throw new IllegalStateException("Cannot add type after runtime is locked");
		if (typesByName.containsKey(type.typeName())) throw new IllegalArgumentException("Type with name " + type.typeName() + " already exists");
		if (!typesByName.containsKey(type.superTypeName())) throw new IllegalArgumentException("Super type with name " + type.superTypeName() + " does not exist");
		RuntimeSkriptType<T> constructedType = type.construct(this);
		typesByName.put(type.typeName(), constructedType);
		if (!(constructedType instanceof StructValue.StructType))
			typesByClass.put(type.valueClass(), constructedType);
		return constructedType;
	}

	private void addConstructedType(String typeName, @NotNull RuntimeSkriptType<?> type) {
		if (typesByName.containsKey(typeName)) throw new IllegalArgumentException("Type with name " + typeName + " already exists");
		typesByName.put(typeName, type);
		typesByClass.put(type.valueClass(), type);
	}

	@Override
	public @NotNull ExecuteContext globalContext() {
		return globalContext;
	}

	@Override
	public <TReceiver extends SkriptValue, TValue extends SkriptValue> Variable.@NotNull OfProperty<TReceiver, TValue> wrapProperty(RuntimeSkriptProperty<TReceiver, TValue> property, TReceiver receiver) {
		return new VariableImpl.OfProperty<>(this, property, receiver);
	}

	@Override
	public @NotNull ResultWithDiagnostics<ScriptContext> load(@NotNull Script script) {
		synchronized (loadingScripts) {
			if (loadingScripts.contains(script)) throw new IllegalStateException("Script is already being loaded");
			if (loadedScripts.containsKey(script)) throw new IllegalStateException("Script is already loaded");
			loadingScripts.add(script);
		}

		ScriptContext scriptContext = new ScriptContextImpl(this, script, globalContext());

		ExecuteResult result = SectionUtils.loadStructuresIn(script.root(), scriptContext);

		synchronized (loadingScripts) {
			if (result instanceof ExecuteResult.Success) loadedScripts.put(script, scriptContext);
			loadingScripts.remove(script);
		}

		if (result instanceof ExecuteResult.Failure(
			ErrorValue reason
		)) return ResultWithDiagnostics.failure(ScriptDiagnostic.error(script.source(), reason.message().toString()));
		return ResultWithDiagnostics.success(scriptContext);
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
