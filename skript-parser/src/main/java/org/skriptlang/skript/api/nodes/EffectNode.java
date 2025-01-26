package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.util.ExecuteResult;

/**
 * An effect node is a verbal statement representing an action.
 */
public interface EffectNode extends StatementNode {

	/**
	 * Executes the statement.
	 */
	@NotNull ExecuteResult execute(@NotNull ExecuteContext context);

}
