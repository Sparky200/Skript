package org.skriptlang.skript.stdlib.structures;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.entries.StructureEntryNode;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.types.ParameterMetaValue;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.Priority;
import org.skriptlang.skript.stdlib.effects.FunctionValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.structure;

public record FunctionStructure(
	String name,
	SectionNode body,
	@NotNull String returnTypeName,
	@Nullable ExpressionNode paramsSelector
) implements StructureNode {

	public static final StructureNodeType<FunctionStructure> TYPE = structure(FunctionStructure.class)
		.syntaxes("[local] function <token::identifier>([<expr:: -> parametermeta>]) [returns <token::identifier>] : <section>")
		.create(context -> {
			String name = context.token(0).tokenContents();

			ExpressionNode paramsSelector = null;
			String returns = SkriptValue.TYPE.typeName();
			SectionNode body = null;

			for (SyntaxNode child : context.children()) {
				switch (child) {
					case SectionNode sec -> body = sec;
					case TokenNode(String tokenContents) -> returns = tokenContents;
					case ExpressionNode expr -> paramsSelector = expr;
					case null, default ->
						throw new IllegalArgumentException("Invalid child: '" + child + "' for function structure");
				}
			}

			return new FunctionStructure(
				name,
				body,
				returns,
				paramsSelector
			);
		})
		.build();

	@Override
	public Priority priority() {
		return StructureNode.FUNCTION;
	}

	@Override
	public @NotNull ExecuteResult load(@NotNull ExecuteContext context) {
		ExecuteContext functionBaseContext = context.fork();
		ParameterMetaValue params = paramsSelector != null ? paramsSelector.resolveAs(ParameterMetaValue.class, context) : null;
		if (paramsSelector != null && params == null)
			return ExecuteResult.failure(new ErrorValue(this, "Parameters could not be resolved"));

		Map<ParameterMetaValue.Parameter, SkriptValue> defaults = new LinkedHashMap<>();
		if (params != null)
			for (ParameterMetaValue.Parameter param : params.jvmValue()) {
				if (param.defaultSelector() != null) {
					SkriptValue defaultValue = param.defaultSelector().resolve(context).toValue();
					defaults.put(param, defaultValue);
				}
			}

		context.setLiteralVariable(name, new FunctionValue((callee, args) -> {
			ExecuteContext functionContext = functionBaseContext.fork();
			if (params != null) {
				for (ParameterMetaValue.Parameter param : params.jvmValue()) {
					SkriptValue value = defaults.get(param);
					if (value != null) functionContext.setLiteralVariableInPlace(param.name(), value);
				}
			}
			return execute(functionContext);
		}));
		return ExecuteResult.success();
	}

	private @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {
		ExecuteContext functionContext = context.fork();
		for (StatementNode node : body.children()) {

			if (node instanceof EffectNode effect) {
				ExecuteResult result = effect.execute(functionContext);
				if (result != ExecuteResult.SUCCESS) {
					return result;
				}
			} else {
				return ExecuteResult.failure(new ErrorValue(this, "Only effects are allowed in function body"));
			}
		}
		return ExecuteResult.success();
	}
}
