package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ParameterMetaValue;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;
import org.skriptlang.skript.stdlib.structures.FunctionStructure;

import java.util.List;

public final class FunctionParameterExpression implements ExpressionNode {

	public static final ExpressionNodeType<FunctionParameterExpression> TYPE = new ExpressionNodeType<>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of(
				"<token::identifier>: <token::identifier>[, <expr>]",
				"<token::identifier>: <token::identifier> = <expr>[, <expr>]"
			);
		}

		@Override
		public SyntaxNodeType<?>[] allowedParents() {
			return new SyntaxNodeType[]{ TYPE, FunctionStructure.TYPE };
		}

		@Override
		public String[] possibleReturnTypes() {
			return new String[]{ ParameterMetaValue.TYPE.typeName() };
		}

		@Override
		public @NotNull FunctionParameterExpression create(List<SyntaxNode> children, int matchedPattern) {
			TokenNode name = (TokenNode) children.getFirst();
			TokenNode type = (TokenNode) children.get(1);

			@Nullable ExpressionNode defaultSelector = matchedPattern == 1 ? (ExpressionNode) children.get(2) : null;
			ExpressionNode additionalSelector = matchedPattern == 1 ? (ExpressionNode) children.get(3) : (ExpressionNode) children.get(2);

			return new FunctionParameterExpression(name, type, defaultSelector, additionalSelector);
		}
	};

	private final TokenNode name;
	private final TokenNode type;
	private final @Nullable ExpressionNode defaultSelector;
	private final ExpressionNode additionalSelector;

	public FunctionParameterExpression(TokenNode name, TokenNode type, @Nullable ExpressionNode defaultSelector, ExpressionNode additionalSelector) {
		this.name = name;
		this.type = type;
		this.defaultSelector = defaultSelector;
		this.additionalSelector = additionalSelector;
	}

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
