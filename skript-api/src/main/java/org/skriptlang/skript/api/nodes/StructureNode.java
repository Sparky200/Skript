package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.Priority;

import java.util.List;
import java.util.Map;

/**
 * Structure nodes are nodes that are permitted on the top-level of a script.
 * <p>
 * <h2>Structure Lifecycle</h2>
 * <p>
 * At parse time:
 * <ul>
 *     <li>
 *         If the matched syntax uses {@code <entries>},
 *         the EntryStructureDefinition is loaded from the type
 *         during <i>descent</i>.
 *     </li>
 *     <li>
 *         If the matched syntax uses {@code <section>},
 *         the structure is loaded like an effect node with
 *         a section.
 *     </li>
 *     <li>
 *         The {@link StructureNodeType#create(List, int, Map)} method is called
 *         to create this structure.
 *     </li>
 * </ul>
 * At execute time:
 * <ul>
 *     <li>
 *         The {@link StructureNode#load(ExecuteContext)} method is called
 *         to load the structure.
 *     </li>
 *     <li>
 *         The {@link StructureNode#postLoad(ExecuteContext)} method is called
 *         to perform any post-load actions.
 *     </li>
 * </ul>
 */
public interface StructureNode extends StatementNode {
	Priority BASE = Priority.base();

	Priority FUNCTION = Priority.before(BASE);
	Priority EVENT = Priority.after(BASE);

	default Priority priority() {
		return BASE;
	}

	/**
	 * Load the structure, making any necessary changes to the context.
	 * This is called when the script is loaded by the executor.
	 * <p>
	 * This method is called as part of {@link StructureNode Structure Lifecycle}.
	 * @param context The file context.
	 * @return The result of the load.
	 */
	default @NotNull ExecuteResult load(@NotNull ExecuteContext context) {
		return ExecuteResult.success();
	}

	/**
	 * Perform any post-load actions.
	 * This is called when the script is loaded by the executor.
	 * <p>
	 * This method is called as part of {@link StructureNode Structure Lifecycle}.
	 * @param context The file context.
	 * @return The result of the post-load.
	 */
	default @NotNull ExecuteResult postLoad(@NotNull ExecuteContext context) {
		return ExecuteResult.success();
	}

	/**
	 * Unload the structure, making any necessary changes to the context.
	 * This is called when the script is unloaded by the executor.
	 * <p>
	 * This method is called as part of {@link StructureNode Structure Lifecycle}.
	 * @param context The file context.
	 * @return The result of unloading.
	 */
	default @NotNull ExecuteResult unload(@NotNull ExecuteContext context) {
		return ExecuteResult.success();
	}
}
