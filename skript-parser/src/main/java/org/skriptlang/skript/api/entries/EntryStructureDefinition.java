package org.skriptlang.skript.api.entries;

import com.google.common.collect.ImmutableList;

import java.util.LinkedList;
import java.util.List;

/**
 * A definition of how a structure's entries should look.
 * <p>
 * The parser will use this to determine how to parse and construct entries.
 */
public final class EntryStructureDefinition {
	private final List<EntryDefinition> entries;

	private EntryStructureDefinition(List<EntryDefinition> entries) {
		this.entries = ImmutableList.copyOf(entries);
	}

	public List<EntryDefinition> entries() {
		return entries;
	}

	public Builder entry(String name, String syntax, boolean optional) {
		return new Builder().entries(entries()).entry(name, syntax, optional);
	}

	public Builder entry(String name, String syntax) {
		return entry(name, syntax, false);
	}

	public static Builder entryStructure() {
		return new Builder();
	}

	public static final class Builder {
		private final List<EntryDefinition> entries = new LinkedList<>();

		public Builder entry(String name, String syntax, boolean optional) {
			entries.add(new EntryDefinition(name, syntax, optional));
			return this;
		}

		public Builder entry(String name, String syntax) {
			return entry(name, syntax, false);
		}

		public Builder entries(List<EntryDefinition> entries) {
			this.entries.addAll(entries);
			return this;
		}

		public EntryStructureDefinition build() {
			return new EntryStructureDefinition(entries);
		}
	}
}
