package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.nodes.TokenNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.NoneValue;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;

import java.util.List;

public class VariableExpression implements ExpressionNode<SkriptValueOrVariable> {
	public static final ExpressionNodeType<VariableExpression, SkriptValueOrVariable> TYPE = new ExpressionNodeType<>() {
		@Override
		public Class<SkriptValueOrVariable> getReturnType() {
			return SkriptValueOrVariable.class;
		}

		@Override
		public List<String> getSyntaxes() {
			return List.of(
				"{<token::identifier>}"
			);
		}

		@Override
		public @NotNull VariableExpression create(List<SyntaxNode> children, int matchedPattern) {
			return new VariableExpression(((TokenNode) children.getFirst()).tokenContents());
		}
	};

	private final String name;

	public VariableExpression(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	@Override
	public @NotNull SkriptValueOrVariable resolve(@NotNull ExecuteContext context) {
		//noinspection DataFlowIssue
		return context.hasVariable(name) ? context.getVariable(name) : NoneValue.get();
	}
}
