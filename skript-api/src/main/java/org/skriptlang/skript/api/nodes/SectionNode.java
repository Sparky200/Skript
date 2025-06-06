package org.skriptlang.skript.api.nodes;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * A node which holds a suite of other nodes,
 * and may or may not have its own context at evaluation (this is dependent on its parent).
 * <p>
 * This node itself cannot be extended, rather its parent node would be the extended one.
 * (see {@link ApiStatus.NonExtendable NonExtendable}).
 * <p>
 * This is one of the few nodes that does not have a type, and thus will not be in the parse context.
 */
@ApiStatus.NonExtendable
public final class SectionNode implements SyntaxNode {
	private final StatementNode[] children;

	public SectionNode(List<StatementNode> children) {
		this.children = new StatementNode[children.size()];
		children.toArray(this.children);
	}

	public StatementNode[] children() {
		return children;
	}
}
