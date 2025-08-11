package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.BooleanValue;
import org.skriptlang.skript.api.types.NoneValue;
import org.skriptlang.skript.api.types.SkriptValue;

import java.util.List;

public class NotExpression implements ExpressionNode {
	public static final ExpressionNodeType<NotExpression> TYPE = new ExpressionNodeType<>() {

		@Override
		public String[] possibleReturnTypes() {
			return new String[] { BooleanValue.TYPE.typeName() };
		}

		@Override
		public List<String> getSyntaxes() {
			return List.of("not <expr::-> boolean>");
		}

		@Override
		public @NotNull NotExpression create(List<SyntaxNode> children, int matchedPattern) {
			return new NotExpression((ExpressionNode) children.getFirst());
		}
	};

	private final ExpressionNode rhsSelector;

	public NotExpression(ExpressionNode rhsSelector) {
		this.rhsSelector = rhsSelector;
	}

	@Override
	public @NotNull SkriptValue resolve(@NotNull ExecuteContext context) {
		SkriptValue rhs = rhsSelector.resolve(context).toValue();
		if (rhs instanceof BooleanValue bool) return new BooleanValue(!bool.jvmValue());
		else if (rhs instanceof NoneValue) return new BooleanValue(true);
		return NoneValue.get();
	}
}
