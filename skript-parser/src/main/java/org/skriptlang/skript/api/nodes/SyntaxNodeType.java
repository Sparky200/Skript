package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.parser.ParseContext;

import java.util.List;

/**
 * Represents a type of node in the Skript syntax tree.
 * The node type stores information about a node's syntaxes.
 * @param <T> The type of node that this node type creates.
 */
public interface SyntaxNodeType<T extends SyntaxNode> {

	/**
	 * Gets the syntaxes that should result in a node of this type.
	 */
	List<String> getSyntaxes();

	@Contract(value = "_ -> new", pure = true)
	@NotNull T create(List<SyntaxNode> children);

	default boolean canBeParsed(ParseContext context) {
		return true;
	}

}
