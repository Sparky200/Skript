package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * A node which holds a suite of other nodes,
 * and may or may not have its own context at evaluation (this is dependent on its parent).
 * <p>
 * This node itself should not be extended by addons, rather its parent node would be the extended one.
 * (see {@link org.jetbrains.annotations.ApiStatus.NonExtendable NonExtendable}).
 */
@ApiStatus.NonExtendable
public interface SectionNode extends SyntaxNode {

	/**
	 * Gets the children of this section.
	 */
	List<StatementNode> children();

}
