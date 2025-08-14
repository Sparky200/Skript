package org.skriptlang.skript.parser.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.entries.StructureEntryNode;
import org.skriptlang.skript.api.nodes.SyntaxNode;
import org.skriptlang.skript.api.util.StructureNodeCreationContext;

import java.util.Arrays;

public class StructureNodeCreationContextBase extends NodeCreationContextBase implements StructureNodeCreationContext {
	private final StructureEntryNode[] entries;

	public StructureNodeCreationContextBase(SyntaxNode[] children, int matchedPattern, StructureEntryNode[] entries) {
		super(children, matchedPattern);
		this.entries = entries;
	}

	@Override
	public @NotNull StructureEntryNode[] entries() {
		return entries;
	}

	@Override
	public @Nullable StructureEntryNode entry(String name) {
		return Arrays.stream(entries)
			.filter(it -> it.name().equals(name))
			.findFirst()
			.orElse(null);
	}

	@Override
	public @NotNull StructureEntryNode entryOrThrow(String name) {
		return Arrays.stream(entries)
			.filter(it -> it.name().equals(name))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("No entry with name '" + name + "'"));
	}
}
