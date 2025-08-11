package org.skriptlang.skript.api.nodes;

public interface ExpressionNodeType<T extends ExpressionNode> extends SyntaxNodeType<T> {

	/// Gets the possible return types this expression may produce.
	/// If the parser is permitted to observe the type hierarchy,
	/// this will be used to reduce candidates before attempting to parse them.
	/// A candidate will be removed if there is no possible way
	/// for the return type to be the requested type of the parent.
	///
	/// For example, if a syntax declares `<expr:: -> boolean>`,
	/// in order for an expression to be a candidate,
	/// it must declare that
	/// it can possibly return a boolean, a supertype of a boolean (which is any), or a subtype of a boolean.
	///
	/// It is highly encouraged to override this method if the type can be determined.
	///
	/// The default return value (any) will match any `<expr>` syntaxes, since any is an ancestor of everything.
	default String[] possibleReturnTypes() {
		return new String[]{"any"};
	}

}
