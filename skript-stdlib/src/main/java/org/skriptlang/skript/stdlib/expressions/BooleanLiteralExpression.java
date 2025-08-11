package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.BooleanValue;

import java.util.List;

public class BooleanLiteralExpression implements ExpressionNode {

	public static final ExpressionNodeType<BooleanLiteralExpression> TYPE = new ExpressionNodeType<>() {
		@Override
		public String[] possibleReturnTypes() {
			return new String[] { BooleanValue.TYPE.typeName() };
		}

		@Override
		public List<String> getSyntaxes() {
			return List.of("true", "false");
		}

		@Override
		public @NotNull BooleanLiteralExpression create(List<SyntaxNode> children, int matchedPattern) {
			return new BooleanLiteralExpression(matchedPattern == 0);
		}
	};

	private final boolean value;

	public BooleanLiteralExpression(boolean value) {
		this.value = value;
	}

	@Override
	public @NotNull BooleanValue resolve(@NotNull ExecuteContext context) {
		return new BooleanValue(value);
	}
}
