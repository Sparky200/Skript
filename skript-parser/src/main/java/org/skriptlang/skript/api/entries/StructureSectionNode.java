package org.skriptlang.skript.api.entries;

import com.google.common.collect.ImmutableMap;
import org.skriptlang.skript.api.nodes.SyntaxNode;

import java.util.Map;

public record StructureSectionNode(Map<String, StructureEntryNode> entries) implements SyntaxNode {
	public StructureSectionNode(Map<String, StructureEntryNode> entries) {
		this.entries = ImmutableMap.copyOf(entries);
	}


}
