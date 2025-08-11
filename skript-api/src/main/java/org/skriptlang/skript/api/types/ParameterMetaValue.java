package org.skriptlang.skript.api.types;

import org.skriptlang.skript.api.nodes.ExpressionNode;

import static org.skriptlang.skript.api.types.base.SkriptTypeFactory.skriptType;

/**
 * @apiNote At the moment, this is used internally for function structures and function parameter expressions.
 * 			This will likely be removed in the future, or used for some reflective-similar insight.
 */
public class ParameterMetaValue extends SkriptValue {
	public static final SkriptType<ParameterMetaValue> TYPE = skriptType("parametermeta", ParameterMetaValue.class)
		.build();

	private final Parameter[] parameters;

	public ParameterMetaValue(Parameter[] parameters) {
		this.parameters = parameters;
	}

	public Parameter[] jvmValue() {
		return parameters;
	}

	public record Parameter(String name, String type, ExpressionNode defaultSelector) {}

}
