package org.skriptlang.skript.stdlib.syntax.effects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.EffectNode;
import org.skriptlang.skript.api.nodes.EffectNodeType;
import org.skriptlang.skript.api.nodes.ExpressionNode;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.SkriptValueOrVariable;
import org.skriptlang.skript.api.types.Variable;
import org.skriptlang.skript.api.util.ExecuteResult;

import java.util.List;

public class IncrementEffect implements EffectNode {
	public static final EffectNodeType<IncrementEffect> TYPE = new EffectNodeType<>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of("increment <expr> [by <expr>]");
		}

		@Override
		public @NotNull IncrementEffect create(List<SyntaxNode> children) {
			SyntaxNode amountSelector = children.size() == 2 ? children.get(1) : null;
			return new IncrementEffect((ExpressionNode<?>) children.getFirst(), (ExpressionNode<?>) amountSelector);
		}
	};

	private final @NotNull ExpressionNode<?> receiverSelector;
	private final @Nullable ExpressionNode<?> amountSelector;

	public IncrementEffect(@NotNull ExpressionNode<?> receiverSelector, @Nullable ExpressionNode<?> amountSelector) {
		this.receiverSelector = receiverSelector;
		this.amountSelector = amountSelector;
	}

	@Override
	public @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {
		SkriptValueOrVariable receiver = receiverSelector.resolve(context);


		if (amountSelector != null) {
			SkriptValue amount = amountSelector.resolve(context).toValue();
			if (receiver instanceof SkriptValue receiverValue) {
				// if it's a value, still try adding directly
				// possible with something like `increment thing by 1` where `thing` ends up being a simple value
				if (!receiverValue.addDirectly(amount)) {
					return ExecuteResult.failure(new ErrorValue(receiver + " does not support directly incrementing by an amount"));
				}
			} else if (receiver instanceof Variable receiverVariable) {
				if (!receiverVariable.add(amount)) {
					return ExecuteResult.failure(new ErrorValue(receiver + " does not support incrementing by an amount"));
				}
			}
		} else {
			if (receiver instanceof SkriptValue receiverValue) {
				// if it's a value, still try adding directly
				// possible with something like `increment thing` where `thing` ends up being a simple value
				if (!receiverValue.incrementDirectly()) {
					return ExecuteResult.failure(new ErrorValue(receiver + " does not support directly incrementing"));
				}
			} else if (receiver instanceof Variable receiverVariable) {
				if (!receiverVariable.increment()) {
					return ExecuteResult.failure(new ErrorValue(receiver + " does not support incrementing"));
				}
			}
		}

		return ExecuteResult.success();
	}
}
