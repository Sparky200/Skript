package org.skriptlang.skript.api.entries;

import com.google.common.collect.ImmutableList;
import org.skriptlang.skript.api.nodes.StatementNode;
import org.skriptlang.skript.api.nodes.SyntaxNode;

import java.util.List;

public record StructureEntryNode(String name, List<SyntaxNode> children) implements StatementNode {
	public StructureEntryNode(String name, List<SyntaxNode> children) {
		this.name = name;
		this.children = ImmutableList.copyOf(children);
	}
}
