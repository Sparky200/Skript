package org.skriptlang.skript.parser.context;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.types.BooleanValue;
import org.skriptlang.skript.api.types.NumberValue;
import org.skriptlang.skript.api.util.NodeCreationContext;

import java.util.Arrays;
import java.util.Objects;

public class NodeCreationContextBase implements NodeCreationContext {
	private final SyntaxNode[] children;
	private final int matchedPattern;

	public NodeCreationContextBase(SyntaxNode[] children, int matchedPattern) {
		this.children = children;
		this.matchedPattern = matchedPattern;
	}

	@Override
	public @NotNull EffectNode effect(int index) {
		return (EffectNode) children[index];
	}

	@Override
	public @NotNull ExpressionNode expression(int index) {
		return (ExpressionNode) children[index];
	}

	@Override
	public @NotNull SectionNode section(int index) {
		return (SectionNode) children[index];
	}

	@Override
	public @NotNull NumberValue number(int index) {
		return new NumberValue(Double.parseDouble(((TokenNode) children[index]).tokenContents()));
	}

	@Override
	public @NotNull BooleanValue bool(int index) {
		return new BooleanValue(((TokenNode) children[index]).tokenContents().equals("true"));
	}

	@Override
	public @NotNull StringNode string(int index) {
		return (StringNode) children[index];
	}

	@Override
	public @NotNull TokenNode token(int index) {
		return (TokenNode) children[index];
	}

	@Override
	public SyntaxNode[] children() {
		return children;
	}

	@Override
	public int matchedPattern() {
		return matchedPattern;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (NodeCreationContextBase) obj;
		return Arrays.equals(this.children, that.children) &&
			this.matchedPattern == that.matchedPattern;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(children), matchedPattern);
	}

	@Override
	public String toString() {
		return "NodeCreationContextBase[" +
			"children=" + Arrays.toString(children) + ", " +
			"matchedPattern=" + matchedPattern + ']';
	}


}
