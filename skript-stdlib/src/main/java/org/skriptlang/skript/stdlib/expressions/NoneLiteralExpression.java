package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.NoneValue;

import java.util.List;

public class NoneLiteralExpression implements ExpressionNode<NoneValue> {
	public static final ExpressionNodeType<NoneLiteralExpression, NoneValue> TYPE = new ExpressionNodeType<>() {
		@Override
		public Class<NoneValue> getReturnType() {
			return NoneValue.class;
		}

		@Override
		public List<String> getSyntaxes() {
			return List.of("none");
		}

		@Override
		public @NotNull NoneLiteralExpression create(List<SyntaxNode> children, int matchedPattern) {
			return new NoneLiteralExpression();
		}
	};

	@Override
	@NotNull
	public NoneValue resolve(@NotNull ExecuteContext context) {
		return NoneValue.get();
	}
}
