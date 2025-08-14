package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ParameterMetaValue;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;
import org.skriptlang.skript.stdlib.structures.FunctionStructure;

import java.util.List;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.expression;

public record FunctionParameterExpression(
	TokenNode name,
	TokenNode type,
	@Nullable ExpressionNode defaultSelector,
	ExpressionNode additionalSelector
) implements ExpressionNode {

	public static final ExpressionNodeType<FunctionParameterExpression> TYPE = expression(FunctionParameterExpression.class)
		.syntaxes(
			// Normal
			"<token::identifier>: <token::identifier>[, expr>]",
			// With default argument
			"<token::identifier>: <token::identifier> = <expr>[, <expr>]"
		)
		.allowedParents(FunctionStructure.TYPE)
		.allowSelfAsParent()
		.possibleReturnTypes(ParameterMetaValue.TYPE)
		.create(context -> {
			TokenNode name = context.token(0);
			TokenNode type = context.token(1);

			@Nullable ExpressionNode defaultSelector = context.matchedPattern() == 1 ? context.expression(2) : null;
			ExpressionNode additionalSelector = context.expression(2 + context.matchedPattern());

			return new FunctionParameterExpression(name, type, defaultSelector, additionalSelector);
		})
		.build();

	@Override
	public @NotNull ParameterMetaValue resolve(@NotNull ExecuteContext context) {
		SkriptValueOrVariable children = additionalSelector.resolve(context).toValue();
		if (!(children instanceof ParameterMetaValue value)) {
			throw new IllegalStateException("Additional parameters must select parameter meta");
		}
		ParameterMetaValue.Parameter[] additional = value.jvmValue();
		ParameterMetaValue.Parameter[] params = new ParameterMetaValue.Parameter[additional.length + 1];
		params[0] = new ParameterMetaValue.Parameter(name.tokenContents(), type.tokenContents(), defaultSelector);
		System.arraycopy(additional, 0, params, 1, additional.length);

		return new ParameterMetaValue(params);
	}
}
