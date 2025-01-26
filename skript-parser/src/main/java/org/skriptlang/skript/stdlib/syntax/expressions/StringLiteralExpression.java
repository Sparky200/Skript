package org.skriptlang.skript.stdlib.syntax.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.nodes.TokenNode;
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
		public @NotNull StringLiteralExpression create(List<SyntaxNode> children) {
			return new StringLiteralExpression(new StringValue(((TokenNode) children.getFirst()).token().asString()));
		}
	};

	private final StringValue value;

	public StringLiteralExpression(StringValue value) {
		this.value = value;
	}

	@Override
	public @NotNull StringValue resolve(@NotNull ExecuteContext context) {
		return value;
	}
}
