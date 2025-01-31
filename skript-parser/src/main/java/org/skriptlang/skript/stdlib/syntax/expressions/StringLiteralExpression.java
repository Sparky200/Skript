package org.skriptlang.skript.stdlib.syntax.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.StringValue;

import java.util.List;

public class StringLiteralExpression implements ExpressionNode<StringValue> {
	public static final ExpressionNodeType<StringLiteralExpression, StringValue> TYPE = new ExpressionNodeType<>() {
		@Override
		public Class<StringValue> getReturnType() {
			return StringValue.class;
		}

		@Override
		public List<String> getSyntaxes() {
			return List.of("<token::string>");
		}

		@Override
		public @NotNull StringLiteralExpression create(List<SyntaxNode> children, int matchedPattern) {
			return new StringLiteralExpression((StringNode) children.getFirst());
		}
	};

	private final ExpressionNode<?> stringTokenSelector;

	public StringLiteralExpression(ExpressionNode<?> stringTokenSelector) {
		this.stringTokenSelector = stringTokenSelector;
	}

	@Override
	public @NotNull StringValue resolve(@NotNull ExecuteContext context) {
		return (StringValue) stringTokenSelector.resolve(context).toValue();
	}
}
