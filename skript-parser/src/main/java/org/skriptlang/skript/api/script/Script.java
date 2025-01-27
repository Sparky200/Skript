package org.skriptlang.skript.api.script;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.SectionNode;
import org.skriptlang.skript.api.nodes.StructureNode;

/**
 * A script that can be executed by the Skript runtime.
 * <p>
 * This is created after parsing a script source, and contains parse results, source information, and other data.
 */
public interface Script {

	/**
	 * The source of the script.
	 */
	@NotNull ScriptSource source();

	/**
	 * The root syntax node, which is always a section node containing {@link StructureNode StructureNodes}.
	 * @return the root node of the script
	 */
	SectionNode root();

}
