package org.skriptlang.skript.api;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.SyntaxNodeType;
import org.skriptlang.skript.api.scope.SectionScope;
import org.skriptlang.skript.api.script.ScriptSource;
import org.skriptlang.skript.api.util.ScriptDiagnostic;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public interface ParseContext {
	@NotNull ScriptSource source();

	Section currentSection();

	int depth();

	Context currentContext();

	Deque<Context> contextStack();

	public static class Section {
		private final int indent;
		private final @Nullable SectionScope scope;

		public Section(int indent, @Nullable SectionScope scope) {
			this.indent = indent;
			this.scope = scope;
		}

		public int getIndent() {
			return indent;
		}

		public @Nullable SectionScope scope() {
			return scope;
		}
	}

	public static class Context {
		private final @Nullable SyntaxNodeType<?> node;
		private final int childIndex;

		private final List<ScriptDiagnostic> diagnostics = new LinkedList<>();

		public Context(@Nullable SyntaxNodeType<?> node, int childIndex) {
			this.node = node;
			this.childIndex = childIndex;
		}

		public void diagnostic(@NotNull ScriptDiagnostic diagnostic) {
			diagnostics.add(diagnostic);
		}

		public List<ScriptDiagnostic> diagnostics() {
			return ImmutableList.copyOf(diagnostics);
		}

		public @Nullable SyntaxNodeType<?> node() {
			return node;
		}

		public int childIndex() {
			return childIndex;
		}
	}
}
