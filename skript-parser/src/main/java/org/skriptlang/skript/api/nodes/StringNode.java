package org.skriptlang.skript.api.nodes;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.StringValue;

import java.util.List;

public class StringNode implements ExpressionNode<StringValue> {
	private final String base;
	private final List<ExpressionNode<?>> childrenSelectors;

	public StringNode(String base, List<ExpressionNode<?>> children) {
		this.base = base.startsWith("\"") && base.endsWith("\"") ? base.substring(1, base.length() - 1) : base;
		this.childrenSelectors = ImmutableList.copyOf(children);
	}

	@Override
	public @NotNull StringValue resolve(@NotNull ExecuteContext context) {
		Object[] stringifiedChildren = childrenSelectors.stream()
			.map(it -> it.resolve(context).toValue().toString())
			.toArray(String[]::new);

		return new StringValue(String.format(base.replace("\\%", "%%"), stringifiedChildren));

	}
}
