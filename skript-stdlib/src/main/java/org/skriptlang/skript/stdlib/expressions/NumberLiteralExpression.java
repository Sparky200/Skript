package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.nodes.TokenNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.NumberValue;

import java.util.List;

public class NumberLiteralExpression implements ExpressionNode {
	public static final ExpressionNodeType<NumberLiteralExpression> TYPE = new ExpressionNodeType<>() {
		@Override
		public String[] possibleReturnTypes() {
			return new String[] { NumberValue.TYPE.typeName() };
		}

		@Override
		public List<String> getSyntaxes() {
			return List.of("<token::number>");
		}

		@Override
		public @NotNull NumberLiteralExpression create(List<SyntaxNode> children, int matchedPattern) {
			return new NumberLiteralExpression(new NumberValue(Double.parseDouble(((TokenNode) children.getFirst()).tokenContents())));
		}
	};

	private final NumberValue value;

	public NumberLiteralExpression(NumberValue value) {
		this.value = value;
	}

	@Override
	public @NotNull NumberValue resolve(@NotNull ExecuteContext context) {
		return value;
	}
}
