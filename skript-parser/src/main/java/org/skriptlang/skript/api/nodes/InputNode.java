package org.skriptlang.skript.api.nodes;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.Variable;

import java.util.List;
import java.util.Objects;

/**
 * Input nodes are special expressions that appear contextually
 * based on a syntax's inputs and outputs in pattern elements.
 * @param inputName The name of the input.
 */
public record InputNode(@NotNull String inputName) implements ExpressionNode<Variable> {
	/**
	 * The type for input node, which is dynamically created by the parser.
	 * @param inputName The name of the input.
	 * @param typeName The name of the type that this input node resolves to.
	 */
	public record Type(@NotNull String inputName, @NotNull String typeName) implements ExpressionNodeType<InputNode, Variable> {
		public Type {
			Preconditions.checkNotNull(inputName, "inputName must not be null");
			Preconditions.checkNotNull(typeName, "typeName must not be null");
		}

		@Override
		public List<String> getSyntaxes() {
			return List.of(inputName);
		}

		@Override
		public Class<Variable> getReturnType() {
			return Variable.class;
		}

		@Override
		public @NotNull InputNode create(List<SyntaxNode> children) {
			return new InputNode(inputName);
		}
	}

	@Override
	public @NotNull Variable resolve(@NotNull ExecuteContext context) {
		if (!context.hasLiteralVariable(inputName()))
			throw new IllegalStateException("Input " + inputName() + " is unexpectedly not present in the context (syntax and node executor not in sync?)");
		return Objects.requireNonNull(context.getLiteralVariable(inputName));
	}
}
