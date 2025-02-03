package org.skriptlang.skript.api.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.script.Script;
import org.skriptlang.skript.api.types.NoneValue;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.Variable;
import org.skriptlang.skript.api.util.ExecuteResult;

import java.util.Map;
import java.util.function.Supplier;

/**
 * A context somewhere in the execution of a syntax tree.
 * Upon executing a syntax tree, a context for the file will be created.
 * Syntax nodes may provisionally create child contexts in order to execute or resolve their children.
 */
public interface ExecuteContext {

	/**
	 * Forks the context into a descendant (child) context.
	 */
	@NotNull ExecuteContext fork();

	/**
	 * Gets the runtime that this context is executing in.
	 */
	@NotNull SkriptRuntime runtime();

	/**
	 * Gets the script that this context is executing.
	 * May be null if this context is beyond the script level.
	 */
	@Nullable Script script();

	/**
	 * Gets the parent context of this context.
	 * May be null if this context is the root context.
	 */
	@Nullable ExecuteContext parent();

	/**
	 * Checks if this context has a variable with the given name.
	 * @param name the name of the variable
	 * @return true if the context has a variable with the given name
	 */
	boolean hasVariableInPlace(String name);

	/**
	 * Checks if this context can see a variable with the given name.
	 * This either means that it {@link #hasVariableInPlace(String) hasVariableInPlace} or that the parent context can see the variable.
	 * @param name the name of the variable
	 * @return true if the context can see a variable with the given name
	 */
	boolean hasVariable(String name);

	/**
	 * Gets a variable with the given name.
	 * @param name the name of the variable
	 * @return the variable, or null if the variable does not exist
	 */
	@Nullable Variable getVariableInPlace(String name);

	/**
	 * Gets a variable with the given name.
	 * @param name the name of the variable
	 * @return the variable, or null if the variable does not exist
	 */
	@Nullable Variable getVariable(String name);

	/**
	 * Sets a variable with the given name in this context.
	 * If the variable already exists in some parent context, it will be shadowed.
	 * @param name the name of the variable
	 * @param initialValue the initial value to assign to the variable
	 * @return the variable that was initialized, which is the same result that would be given with {@link #getVariableInPlace(String)}.
	 */
	@NotNull Variable setVariableInPlace(String name, @NotNull SkriptValue initialValue);

	/**
	 * Sets a variable with the given name in this context.
	 * If the variable already exists in some parent context, it will be shadowed.
	 * @param name the name of the variable
	 * @return the variable that was initialized, which is the same result that would be given with {@link #getVariableInPlace(String)}.
	 */
	default @NotNull Variable setVariableInPlace(String name) {
		return setVariableInPlace(name, NoneValue.get());
	}

	/**
	 * Sets a variable with the given name in this context.
	 * If the variable already exists in some parent context, the variable will be set in that context.
	 * @param name the name of the variable
	 * @param initialValue the value of the variable
	 * @return the variable that was initialized, which is the same result that would be given with {@link #getVariable(String)}.
	 */
	@NotNull Variable setVariable(String name, @NotNull SkriptValue initialValue);

	/**
	 * Sets a variable with the given name in this context.
	 * If the variable already exists in some parent context, the variable will be set in that context.
	 * @param name the name of the variable
	 * @return the variable that was initialized, which is the same result that would be given with {@link #getVariable(String)}.
	 */
	default @NotNull Variable setVariable(String name) {
		return setVariable(name, NoneValue.get());
	}

	/**
	 * Unsets a variable with the given name in this context.
	 * @param name the name of the variable
	 */
	void unsetVariableInPlace(String name);

	/**
	 * Unsets a variable with the given name in this context, or in a parent context if it doesn't exist in this context.
	 * @param name the name of the variable
	 */
	void unsetVariable(String name);

	/**
	 * Checks if this context has a literal variable with the given name.
	 * @param name the name of the literal variable
	 * @return true if the context has a literal variable with the given name
	 */
	boolean hasLiteralVariableInPlace(String name);

	/**
	 * Checks if this context can see a literal variable with the given name.
	 * This either means that it {@link #hasLiteralVariableInPlace(String) hasLiteralVariableInPlace} or that the parent context can see the literal variable.
	 * @param name the name of the literal variable
	 * @return true if the context can see a literal variable with the given name
	 */
	boolean hasLiteralVariable(String name);

	/**
	 * Gets a literal variable with the given name.
	 * @param name the name of the literal variable
	 * @return the literal variable, or null if the literal variable does not exist
	 */
	@Nullable Variable getLiteralVariableInPlace(String name);

	/**
	 * Gets a literal variable with the given name.
	 * @param name the name of the literal variable
	 * @return the literal variable, or null if the literal variable does not exist
	 */
	@Nullable Variable getLiteralVariable(String name);

	/**
	 * Sets a literal variable with the given name in this context.
	 * If the literal variable already exists in some parent context, it will be shadowed.
	 * @param name the name of the literal variable
	 * @param initialValue the initial value to assign to the literal variable
	 * @return the literal variable that was initialized, which is the same result that would be given with {@link #getLiteralVariableInPlace(String)}.
	 */
	@NotNull Variable setLiteralVariableInPlace(String name, @NotNull SkriptValue initialValue);

	/**
	 * Sets a literal variable with the given name in this context.
	 * If the literal variable already exists in some parent context, it will be shadowed.
	 * @param name the name of the literal variable
	 * @return the literal variable that was initialized, which is the same result that would be given with {@link #getLiteralVariableInPlace(String)}.
	 */
	default @NotNull Variable setLiteralVariableInPlace(String name) {
		return setLiteralVariableInPlace(name, NoneValue.get());
	}

	/**
	 * Sets a literal variable with the given name in this context.
	 * If the literal variable already exists in some parent context, the literal variable will be set in that context.
	 * @param name the name of the literal variable
	 * @param initialValue the value of the literal variable
	 * @return the literal variable that was initialized, which is the same result that would be given with {@link #getLiteralVariable(String)}.
	 */
	@NotNull Variable setLiteralVariable(String name, @NotNull SkriptValue initialValue);

	/**
	 * Sets a literal variable with the given name in this context.
	 * If the literal variable already exists in some parent context, the literal variable will be set in that context.
	 * @param name the name of the literal variable
	 * @return the literal variable that was initialized, which is the same result that would be given with {@link #getLiteralVariable(String)}.
	 */
	default @NotNull Variable setLiteralVariable(String name) {
		return setLiteralVariable(name, NoneValue.get());
	}

	/**
	 * Unsets a literal variable with the given name in this context.
	 * @param name the name of the literal variable
	 */
	void unsetLiteralVariableInPlace(String name);

	/**
	 * Unsets a literal variable with the given name in this context, or in a parent context if it doesn't exist in this context.
	 * @param name the name of the literal variable
	 */
	void unsetLiteralVariable(String name);

	// TODO: functions (should this be natively supported, or via ScriptData?)
	void setFunction(String name, Supplier<ExecuteResult> executor);

	/**
	 * Adds custom data to the script context.
	 * If no parent context is a script context (happens if no script is being executed),
	 * the data will be lost.
	 * @param key the key / owner of the data
	 * @param value the value of the data
	 */
	void addScriptData(SyntaxNode key, Object value);

	/**
	 * Checks if the script context has data with the given key.
	 * @param key the key / owner of the data
	 * @return true if the script context has data with the given key
	 */
	boolean hasScriptData(SyntaxNode key);

	/**
	 * Gets script data with the given key from the script context.
	 * @param key the key / owner of the data
	 * @return the data with the given key, or null if the script context does not have data with the given key
	 */
	Object getScriptData(SyntaxNode key);

	/**
	 * Gets script data with the given key from the script context.
	 * @param key the key / owner of the data
	 * @param type the class type of the data
	 * @return the data with the given key, or null if the script context does not have data with the given key
	 * @param <T> the type of the data
	 */
	<T> @Nullable T getScriptData(SyntaxNode key, Class<T> type);

	/**
	 * Removes the data with the given key from the script context.
	 * @param key the key / owner of the data
	 */
	void removeScriptData(SyntaxNode key);

	/**
	 * Gets all script data from the script context.
	 * @return a map of all script data
	 */
	Map<SyntaxNode, Object> getAllScriptData();
}
