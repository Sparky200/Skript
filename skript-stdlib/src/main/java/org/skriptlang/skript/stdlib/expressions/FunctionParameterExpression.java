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
		.create((children, matchedPattern) -> {
			TokenNode name = (TokenNode) children.getFirst();
			TokenNode type = (TokenNode) children.get(1);

			@Nullable ExpressionNode defaultSelector = matchedPattern == 1 ? (ExpressionNode) children.get(2) : null;
			ExpressionNode additionalSelector = matchedPattern == 1 ? (ExpressionNode) children.get(3) : (ExpressionNode) children.get(2);

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
