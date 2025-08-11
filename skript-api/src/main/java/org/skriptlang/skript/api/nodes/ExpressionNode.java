package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.NoneValue;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;

/// Represents a node in the syntax tree that can be resolved to a value.
///
/// Because expressions are tricky in that the parser can't guarantee types, especially for input nodes,
/// it may actually be better to store expressions as erased types and use [#resolveAs(Class,ExecuteContext)]
/// at runtime.
///
/// This should especially be noted because an expression may have a type set
/// as a superclass of the type desired in the parent's syntax,
/// but may still return the desired type at runtime, in which case operation should continue.
public interface ExpressionNode extends SyntaxNode {

	/// Resolves the value of this expression.
	///
	/// Cases where the value cannot be resolved should return [NoneValue].
	/// Do not return null.
	/// @param context The context to resolve the value in.
	/// @return The resolved value.
	@NotNull SkriptValueOrVariable resolve(@NotNull ExecuteContext context);

	/// A typed result getter, useful for when the type is unknown at compile time.
	/// @param type The type to resolve the value as.
	/// @return The resolved value.
	/// @param <TSubType> The type to resolve the value as.
	default <TSubType extends SkriptValue> @Nullable TSubType resolveAs(@NotNull Class<TSubType> type, @NotNull ExecuteContext context) {
		SkriptValueOrVariable resolve = resolve(context).toValue();
		if (!type.isInstance(resolve)) {
			return null;
		}
		return type.cast(resolve);
	}

}
