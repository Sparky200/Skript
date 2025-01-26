package org.skriptlang.skript.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.script.ScriptSource;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.Variable;
import org.skriptlang.skript.api.util.ExecuteResult;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExecuteContextImpl implements ExecuteContext {

	private final @NotNull SkriptRuntime runtime;
	private final @Nullable ExecuteContext parent;

	private final @NotNull Map<String, Variable> variables = new LinkedHashMap<>();
	private final @NotNull Map<String, Variable> literalVariables = new LinkedHashMap<>();

	protected ExecuteContextImpl(@NotNull SkriptRuntime runtime, @Nullable ExecuteContext parent) {
		this.runtime = runtime;
		this.parent = parent;
	}

	public ExecuteContextImpl(@NotNull SkriptRuntime runtime) {
		this(runtime, null);
	}

	@Override
	public @NotNull ExecuteContext fork() {
		return new ExecuteContextImpl(runtime, this);
	}

	@Override
	public @NotNull SkriptRuntime runtime() {
		return runtime;
	}

	@Override
	public @Nullable ScriptSource script() {
		return parent != null ? parent.script() : null;
	}

	@Override
	public @Nullable ExecuteContext parent() {
		return parent;
	}

	@Override
	public boolean hasVariableInPlace(String name) {
		return variables.containsKey(name);
	}

	@Override
	public boolean hasVariable(String name) {
		return hasVariableInPlace(name) || (parent != null && parent.hasVariable(name));
	}

	@Override
	public @Nullable Variable getVariableInPlace(String name) {
		return variables.get(name);
	}

	@Override
	public @Nullable Variable getVariable(String name) {
		if (hasVariableInPlace(name)) {
			return getVariableInPlace(name);
		} else if (parent != null) {
			return parent.getVariable(name);
		} else {
			return null;
		}
	}

	@Override
	public @NotNull Variable setVariableInPlace(String name, @NotNull SkriptValue initialValue) {
		Variable variable = new VariableImpl.OfValue(runtime, initialValue);
		variables.put(name, variable);
		return variable;
	}

	@Override
	public @NotNull Variable setVariable(String name, @NotNull SkriptValue initialValue) {
		// set in parent if it has the variable, and we don't have one by the same name
		if (!hasVariableInPlace(name) && parent != null && parent.hasVariable(name)) {
			return parent.setVariable(name, initialValue);
		} else return setVariableInPlace(name, initialValue);
	}

	@Override
	public void unsetVariableInPlace(String name) {
		variables.remove(name);
	}

	@Override
	public void unsetVariable(String name) {
		if (hasVariableInPlace(name)) {
			unsetVariableInPlace(name);
		} else if (parent != null && parent.hasVariable(name)) {
			parent.unsetVariable(name);
		}
	}

	@Override
	public boolean hasLiteralVariableInPlace(String name) {
		return literalVariables.containsKey(name);
	}

	@Override
	public boolean hasLiteralVariable(String name) {
		return hasLiteralVariableInPlace(name) || (parent != null && parent.hasLiteralVariable(name));
	}

	@Override
	public @Nullable Variable getLiteralVariableInPlace(String name) {
		return literalVariables.get(name);
	}

	@Override
	public @Nullable Variable getLiteralVariable(String name) {
		if (hasLiteralVariableInPlace(name)) {
			return getLiteralVariableInPlace(name);
		} else if (parent != null) {
			return parent.getLiteralVariable(name);
		} else {
			return null;
		}
	}

	@Override
	public @NotNull Variable setLiteralVariableInPlace(String name, @NotNull SkriptValue initialValue) {
		Variable variable = new VariableImpl.OfValue(runtime, initialValue);
		literalVariables.put(name, variable);
		return variable;
	}

	@Override
	public @NotNull Variable setLiteralVariable(String name, @NotNull SkriptValue initialValue) {
		// set in parent if it has the variable, and we don't have one by the same name
		if (!hasLiteralVariableInPlace(name) && parent != null && parent.hasLiteralVariable(name)) {
			return parent.setLiteralVariable(name, initialValue);
		} else return setLiteralVariableInPlace(name, initialValue);
	}

	@Override
	public void unsetLiteralVariableInPlace(String name) {
		literalVariables.remove(name);
	}

	@Override
	public void unsetLiteralVariable(String name) {
		if (hasLiteralVariableInPlace(name)) {
			unsetLiteralVariableInPlace(name);
		} else if (parent != null && parent.hasLiteralVariable(name)) {
			parent.unsetLiteralVariable(name);
		}
	}

	@Override
	public void setFunction(String name, Supplier<ExecuteResult> executor) {
		// TODO: functions
	}

	@Override
	public void addScriptData(SyntaxNode key, Object value) {
		if (parent == null) throw new IllegalStateException("Cannot add script data without a script context");
		parent.addScriptData(key, value);
	}

	@Override
	public boolean hasScriptData(SyntaxNode key) {
		if (parent == null) return false;
		return parent.hasScriptData(key);
	}

	@Override
	public Object getScriptData(SyntaxNode key) {
		if (parent == null) return null;
		return parent.getScriptData(key);
	}

	@Override
	public <T> @Nullable T getScriptData(SyntaxNode key, Class<T> type) {
		if (parent == null) return null;
		return parent.getScriptData(key, type);
	}

	@Override
	public void removeScriptData(SyntaxNode key) {
		if (parent == null) throw new IllegalStateException("Cannot remove script data without a script context");
		parent.removeScriptData(key);
	}

	@Override
	public Map<SyntaxNode, Object> getAllScriptData() {
		if (parent == null) return Map.of();
		return parent.getAllScriptData();
	}
}
