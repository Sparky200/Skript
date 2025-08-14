package org.skriptlang.skript.stdlib.expressions;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.ExpressionNodeType;
import org.skriptlang.skript.api.nodes.TokenNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.NoneValue;
import org.skriptlang.skript.api.types.RuntimeSkriptType;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;
import org.skriptlang.skript.api.types.StructValue;

import java.util.Objects;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.expression;

public record NewStructExpression(TokenNode nameSelector) implements ExpressionNode {
	public static final ExpressionNodeType<NewStructExpression> TYPE = expression(NewStructExpression.class)
		.syntaxes("[a] new <token::identifier>")
		.create(context -> new NewStructExpression(context.token(0)))
		.build();

	@Override
	public @NotNull SkriptValueOrVariable resolve(@NotNull ExecuteContext context) {
		RuntimeSkriptType<?> runtimeType = Objects.requireNonNull(context.scriptContext()).getTypeByName(nameSelector().tokenContents());
		if (runtimeType == null) return NoneValue.get();
		if (!(runtimeType instanceof StructValue.StructType structType)) return NoneValue.get();

		return new StructValue(structType);
	}
}
