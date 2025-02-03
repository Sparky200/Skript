package org.skriptlang.skript.api;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.SectionNode;
import org.skriptlang.skript.api.nodes.SyntaxNodeType;
import org.skriptlang.skript.api.scope.SectionScope;
import org.skriptlang.skript.api.script.ScriptSource;
import org.skriptlang.skript.api.util.ResultWithDiagnostics;

import java.util.List;

/**
 * The Skript Parser is responsible for parsing Skript code into a syntax tree.
 */
public interface SkriptParser {

	/**
	 * Returns whether the parser is locked.
	 * The lock state of the parser determines if
	 * new node types can be submitted via {@link SkriptParser#submitNode(SyntaxNodeType) submitNode}.
	 */
	boolean isLocked();

	/**
	 * Submits a node type to the parser.
	 * Once a node is submitted, the parser will be capable of parsing that node type.
	 * @param nodeType The node type to submit
	 */
	void submitNode(@NotNull SyntaxNodeType<?> nodeType);

	/**
	 * Submits a section scope to the parser.
	 * The scope will then be available for use in syntax patterns.
	 * @param scope The scope to submit
	 */
	void submitScope(@NotNull SectionScope scope);

	/**
	 * Gets an <b>immutable</b> view of all node types that have been submitted to the parser.
	 */
	List<SyntaxNodeType<?>> getNodeTypes();

	ResultWithDiagnostics<SectionNode> parse(ScriptSource source);
}
