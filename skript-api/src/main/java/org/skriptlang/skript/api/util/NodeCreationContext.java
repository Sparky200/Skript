package org.skriptlang.skript.api.util;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.types.BooleanValue;
import org.skriptlang.skript.api.types.NumberValue;

/**
 * The context given to node constructors when they are being created on tree ascent.
 * This provides the children and the pattern index that was matched.
 * @see StructureNodeCreationContext Structure version
 */
public interface NodeCreationContext {

	/**
	 * The children to create the node with.
	 * Provided especially for nodes which have advanced logic or might have variable-length children.
	 */
	SyntaxNode[] children();

	/**
	 * The index of the pattern that was matched (based on the node type that successfully created the node).
	 */
	int matchedPattern();

	/**
	 * Gets a child as an effect node at the given index.
	 * @param index The index of the child to get.
	 * @return The child as an effect node.
	 */
	@NotNull EffectNode effect(int index);

	/**
	 * Gets a child as an expression node at the given index.
	 * @param index The index of the child to get.
	 * @return The child as an expression node.
	 */
	@NotNull ExpressionNode expression(int index);

	/**
	 * Gets a child as a section node at the given index.
	 * @param index The index of the child to get.
	 * @return The child as a section node.
	 */
	@NotNull SectionNode section(int index);

	/**
	 * Gets a child as a token node at the given index.
	 * Reminder that this is for variable static syntax,
	 * and generally will only parse the specific cases covered by the tokenizer.
	 * @param index The index of the child to get.
	 * @return The child as a token node.
	 * @see TokenNode
	 */
	@NotNull TokenNode token(int index);

	/**
	 * Gets a child as a token node at the given index and parses it to a number.
	 * It may be better to use {@link #token(int)} for easier tree serialization
	 * -- using this method will lose position data in the future.
	 * <p>
	 * <b>
	 *     NOTE: Do not use this if you are using <code>&lt;expr::-> number&gt;</code>
	 * 			 -- this is intended for <code>&lt;token::number&gt;</code>,
	 * 			 which will parse exclusively the number literal.
	 * </b>
	 * @param index The index of the child to get.
	 * @return The child as a number value.
	 */
	@NotNull NumberValue number(int index);

	/**
	 * Gets a child as a token node at the given index and parses it to a boolean.
	 * It may be better to use {@link #token(int)} for easier tree serialization
	 * -- using this method will lose position data in the future.
	 * <p>
	 * <b>
	 *     NOTE: Do not use this if you are using <code>&lt;expr::-> bool&gt;</code>
	 * 			 -- this is intended for <code>&lt;token::bool&gt;</code>,
	 * 			 which will parse exclusively the boolean literal.
	 * </b>
	 * @param index The index of the child to get.
	 * @return The child as a boolean value.
	 */
	@NotNull BooleanValue bool(int index);

	/**
	 * Gets a child as a string node at the given index.
	 * This corresponds to <code>&gt;token::string&lt;</code>.
	 * @param index The index of the child to get.
	 * @return The child as a string node.
	 */
	@NotNull StringNode string(int index);

}
