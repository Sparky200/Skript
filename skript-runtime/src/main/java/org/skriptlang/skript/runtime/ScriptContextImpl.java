package org.skriptlang.skript.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.runtime.ScriptContext;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.script.Script;
import org.skriptlang.skript.api.types.RuntimeSkriptType;
import org.skriptlang.skript.api.types.SkriptType;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.StructValue;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Context belonging to a script itself.
 */
public class ScriptContextImpl extends ExecuteContextImpl implements ScriptContext {
	private final @Nullable Script script;

	private final Map<SyntaxNode, Object> scriptData = new LinkedHashMap<>();

	private final Map<String, RuntimeSkriptType<?>> typesByName = new LinkedHashMap<>();

	public ScriptContextImpl(@NotNull SkriptRuntime runtime, @Nullable Script script, @Nullable ExecuteContext parent) {
		super(runtime, parent);
		this.script = script;
	}

	public ScriptContextImpl(@NotNull SkriptRuntime runtime, @Nullable Script script) {
		this(runtime, script, null);
	}

	@Override
	public @Nullable Script script() {
		return script;
	}

	@Override
	public @NotNull ScriptContext scriptContext() {
		return this;
	}

	@Override
	public void addScriptData(SyntaxNode key, Object value) {
		scriptData.put(key, value);
	}

	@Override
	public void removeScriptData(SyntaxNode key) {
		scriptData.remove(key);
	}

	@Override
	public boolean hasScriptData(SyntaxNode key) {
		return scriptData.containsKey(key);
	}

	@Override
	public Object getScriptData(SyntaxNode key) {
		return scriptData.get(key);
	}

	@Override
	public <T> @Nullable T getScriptData(SyntaxNode key, @NotNull Class<T> type) {
		return type.cast(scriptData.get(key));
	}

	@Override
	public @Nullable RuntimeSkriptType<?> getTypeByName(@NotNull String name) {
		RuntimeSkriptType<?> type = typesByName.get(name);
		return type != null ? type : runtime().getTypeByName(name);
	}

	@Override
	public @NotNull String getNameOfType(@NotNull RuntimeSkriptType<?> type) {
		return typesByName.values().stream().filter(it -> it == type).findFirst().orElseThrow().name();
	}

	@Override
	public <T extends SkriptValue> void addType(@NotNull RuntimeSkriptType<T> type) {
		typesByName.put(type.name(), type);
	}

	@Override
	public void removeType(@NotNull RuntimeSkriptType<?> type) {
		typesByName.remove(type.name());
	}
}
