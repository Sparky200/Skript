package org.skriptlang.skript.api.entries;

import org.skriptlang.skript.api.nodes.SyntaxNode;

public record StructureSectionNode(StructureEntryNode[] entries) implements SyntaxNode {}
