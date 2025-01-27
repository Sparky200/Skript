package org.skriptlang.skript.stdlib.syntax.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.nodes.TokenNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.SkriptProperty;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;
import org.skriptlang.skript.api.types.Variable;

import java.util.List;

public final class PropertyExpression implements ExpressionNode<Variable.OfProperty<?, ?>> {
	public static final ExpressionNodeType<PropertyExpression, Variable.OfProperty<?, ?>> TYPE = new ExpressionNodeType<>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of(
				"<token::identifier> of <expr>"
			);
		}

		@Override
		public Class<Variable.OfProperty<?, ?>> getReturnType() {
			return null;
		}

		@Override
		public @NotNull PropertyExpression create(List<SyntaxNode> children, int matchedPattern) {
			return new PropertyExpression(((TokenNode) children.getFirst()).token().asString(), (ExpressionNode<?>) children.get(1));
		}
	};

	private final String propertyName;
	private final ExpressionNode<?> receiverSelector;

	public PropertyExpression(String propertyName, ExpressionNode<?> receiverSelector) {
		this.propertyName = propertyName;
		this.receiverSelector = receiverSelector;
	}

	@Override
	public @NotNull Variable.OfProperty<?, ?> resolve(@NotNull ExecuteContext context) {
		SkriptValueOrVariable receiverOrVar = receiverSelector.resolve(context);

		SkriptValue receiver = null;
		if (receiverOrVar instanceof SkriptValue val) {
			receiver = val;
		} else if (receiverOrVar instanceof Variable variable) {
			receiver = variable.get();
		}

		// TODO: need a way to feed an ExecuteResult out of an expression
		if (receiver == null) throw new IllegalStateException("Cannot get property of a non-variable");
		SkriptProperty<?, ?> prop = receiver.getType(context.runtime()).getProperty(propertyName);
		if (prop == null) {
			// TODO: SkriptValueType should have a type name
			throw new IllegalStateException("Property '" + propertyName + "' does not exist on type '" + receiver.getType(context.runtime()) + "'");
		}
		return prop.asVariable(receiver);
	}
}
