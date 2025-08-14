package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.NodeCreationContext;
import org.skriptlang.skript.api.util.Priority;

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
 *         The {@link StructureNodeType#create(NodeCreationContext)} method is called
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
 * <ul>
 *     <li>
 *         The {@link StructureNode#unload(ExecuteContext)} method is called
 *         to unload the structure, if this script is unloaded from the runtime.
 *     </li>
 * </ul>
 * <p>
 * <h2>Priority</h2>
 * Structure nodes are loaded in a certain priority order.
 * The default priority is {@link #BASE},
 * and the built-in priority order is:
 * <ol>
 *     <li>{@link #STRUCTURE}</li>
 *     <li>{@link #FUNCTION}</li>
 *     <li>{@link #BASE}</li>
 *     <li>{@link #EVENT}</li>
 * </ol>
 * If two structures have the same priority, they will be loaded in the order in which they are defined in the script.
 * For example, if a script contains two structs, the first one will <i>always</i> be loaded before the second one.
 * <p>
 * Especially in the case of structs, this causes the requirement where structs that use other structs must be defined
 * after the structs they use.
 */
public interface StructureNode extends StatementNode {
	Priority BASE = Priority.base();

	Priority FUNCTION = Priority.before(BASE);
	Priority STRUCTURE = Priority.before(FUNCTION);
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
