package org.skriptlang.skript.api.util;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.EffectNode;
import org.skriptlang.skript.api.nodes.SectionNode;
import org.skriptlang.skript.api.nodes.StatementNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;

/**
 * Utilities for sections
 */
public final class SectionUtils {
	private SectionUtils() {
		// no instance
	}

	/**
	 * Executes a section in the traditional way.
	 * This means to iterate children and execute effects, while stopping if a conditional is encountered.
	 * <p>
	 * The reason this is in a utility is to avoid the notion
	 * that this is the only way to treat a section.
	 *
	 * @param section The section to execute
	 * @param runContext The context to execute in
	 * @return The result
	 */
	public static @NotNull ExecuteResult executeSimple(@NotNull SectionNode section, @NotNull ExecuteContext runContext) {
		for (StatementNode node : section.children()) {
			if (node instanceof EffectNode effectNode) {
				ExecuteResult result = effectNode.execute(runContext);
				if (result instanceof ExecuteResult.Failure) {
					return result;
				}
			} else {
				// this utility doesn't know how to handle whatever statement node was enountered
				throw new IllegalStateException("Unexpected statement node: " + node);
			}
		}
		return ExecuteResult.success();
	}
}
