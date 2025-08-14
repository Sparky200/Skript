package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ListValue;
import org.skriptlang.skript.api.types.NoneValue;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;
import org.skriptlang.skript.api.types.RuntimeSkriptType;

import java.util.LinkedList;
import java.util.List;

/**
 * In the case of conflicts between multiple possible ways to parse tokens,
 * a multi-value node will be created.
 * This is that multi-value node for expressions.
 * Note that this only occurs with syntaxes that are ambiguous until a type is known.
 * In addition, if there's still type ambiguity, the "first" result matching the desired type will be returned.
 */
public class MultiMatchExpressionNode implements ExpressionNode {

	private final List<? extends ExpressionNode> possibleMatches;

	/**
	 * The type name that is desired by the parent.
	 */
	private final @NotNull String desiredTypeName;

	public MultiMatchExpressionNode(List<? extends ExpressionNode> possibleMatches, @Nullable String desiredTypeName) {
		this.possibleMatches = possibleMatches;
		this.desiredTypeName = desiredTypeName != null ? desiredTypeName : "any";
	}

	private List<RuntimeSkriptType<?>> createAllowedTypes(ExecuteContext context, RuntimeSkriptType<?> desiredType) {
		List<RuntimeSkriptType<?>> allowedTypes = new LinkedList<>();
		// may be a list
		allowedTypes.add(context.runtime().getTypeByName("list"));
		RuntimeSkriptType<?> current = desiredType;

		while (current != null) {
			allowedTypes.add(current);
			current = current.superType();
		}

		return allowedTypes;
	}

	@Override
	public @NotNull SkriptValueOrVariable resolve(@NotNull ExecuteContext context) {
		// note properties will be preferred over values (will return property if possible)

		RuntimeSkriptType<?> desiredType = context.runtime().getTypeByName(desiredTypeName);
		if (desiredType == null) throw new IllegalStateException("Desired type not found: " + desiredTypeName);

		List<RuntimeSkriptType<?>> allowedTypes = createAllowedTypes(context, desiredType);


		for (ExpressionNode possibleMatch : possibleMatches) {
			SkriptValueOrVariable resolved = possibleMatch.resolve(context);

			if (!allowedTypes.contains(resolved.toValue().getType(context.runtime().globalContext()))) {
				continue;
			}

			// if we don't want a list, and it's a list, it's still valid if all values are of the desired type
			if (
				!desiredTypeName.equals("list")
					&& resolved instanceof ListValue value
					&& value.stream().anyMatch(v -> !(desiredType.isInstance(v) || v instanceof NoneValue))
			) {
				continue;
			}

			return resolved;
		}

		// if no matches, return none
		// TODO: should this be an error?
		return NoneValue.get();
	}
}
