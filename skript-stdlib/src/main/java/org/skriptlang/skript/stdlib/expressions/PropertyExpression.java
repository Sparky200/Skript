package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.nodes.TokenNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.*;

import java.util.List;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.expression;

public record PropertyExpression(String propertyName, ExpressionNode receiverSelector) implements ExpressionNode {

	public static final ExpressionNodeType<PropertyExpression> TYPE = expression(PropertyExpression.class)
		.syntaxes("<token::identifier> of <expr>")
		.create((children, matchedPattern) ->
			new PropertyExpression(((TokenNode) children.getFirst()).tokenContents(), (ExpressionNode) children.get(1))
		)
		.build();

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

		RuntimeSkriptType<?> type = receiver.getType(context.runtime());

		RuntimeSkriptProperty<?, ?> prop = type.getProperty(propertyName);
		if (prop == null) {
			// TODO: SkriptValueType should have a type name
			throw new IllegalStateException("Property '" + propertyName + "' does not exist on type '" + type.name() + "'");
		}
		return prop.asVariable(receiver);
	}
}
