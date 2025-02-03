package org.skriptlang.skript.api.scope;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * A scope is slightly more version of inputs meant for a section.
 * <p>
 * Since putting many inputs in a section is cumbersome, scopes allow you to group inputs together
 * in the case of a section.
 * <p>
 * This is passed to the parser separate from nodes.
 */
public record SectionScope(String name, List<InputDefinition> inputs) {
	public SectionScope(String name, List<InputDefinition> inputs) {
		this.name = name;
		this.inputs = ImmutableList.copyOf(inputs);
	}

	/**
	 * The name of the scope.
	 * <p>
	 * This is used to identify the scope in the syntax.
	 *
	 * @return The name of the scope.
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * The inputs of the scope.
	 * <p>
	 * This is used to identify the inputs of the scope in the syntax.
	 *
	 * @return The inputs of the scope.
	 */
	@Override
	public List<InputDefinition> inputs() {
		return inputs;
	}
}
