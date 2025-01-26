package org.skriptlang.skript.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.script.ScriptSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Context belonging to a script itself.
 */
public class ScriptContext extends ExecuteContextImpl {
	private final @NotNull ScriptSource script;

	private final Map<SyntaxNode, Object> scriptData = new LinkedHashMap<>();

	public ScriptContext(@NotNull SkriptRuntime runtime, @NotNull ScriptSource script, @Nullable ExecuteContext parent) {
		super(runtime, parent);
		this.script = script;
	}

	public ScriptContext(@NotNull SkriptRuntime runtime, @NotNull ScriptSource script) {
		this(runtime, script, null);
	}

	@Override
	public @Nullable ScriptSource script() {
		return script;
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


}
