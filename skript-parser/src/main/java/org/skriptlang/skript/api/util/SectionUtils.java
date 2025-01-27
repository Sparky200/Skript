package org.skriptlang.skript.api.util;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.EffectNode;
import org.skriptlang.skript.api.nodes.SectionNode;
import org.skriptlang.skript.api.nodes.StatementNode;
import org.skriptlang.skript.api.nodes.StructureNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ErrorValue;

import java.util.Comparator;
import java.util.List;

/**
 * Utilities for sections
 */
public final class SectionUtils {
	private SectionUtils() {
		// no instance
	}

	/**
	 * Executes a section in the traditional way.
	 * This means to iterate children and execute effects, while stopping if a conditional is encountered.
	 * <p>
	 * The reason this is in a utility is to avoid the notion
	 * that this is the only way to treat a section.
	 *
	 * @param section The section to execute
	 * @param runContext The context to execute in
	 * @return The result
	 */
	public static @NotNull ExecuteResult executeSimple(@NotNull SectionNode section, @NotNull ExecuteContext runContext) {
		for (StatementNode node : section.children()) {
			if (node instanceof EffectNode effectNode) {
				ExecuteResult result = effectNode.execute(runContext);
				if (result instanceof ExecuteResult.Failure) {
					return result;
				}
			} else {
				// this utility doesn't know how to handle whatever statement node was enountered
				throw new IllegalStateException("Unexpected statement node: " + node);
			}
		}
		return ExecuteResult.success();
	}

	/**
	 * Loads all structures in a section.
	 * @param section The section to load
	 * @param loadContext The context to load in
	 * @return The result
	 */
	public static @NotNull ExecuteResult loadStructuresIn(@NotNull SectionNode section, @NotNull ExecuteContext loadContext) {
		List<StructureNode> structures = section.children().stream()
			.map(node -> node instanceof StructureNode ? (StructureNode) node : null)
			.toList();

		if (structures.contains(null)) ExecuteResult.failure(new ErrorValue("Section contains non-structure nodes"));

		List<StructureNode> sortedStructures = structures.stream()
			.sorted(Comparator.comparing(StructureNode::priority))
			.toList();

		for (StructureNode structure : sortedStructures) {
			ExecuteResult result = structure.load(loadContext);
			if (result instanceof ExecuteResult.Failure) {
				return result;
			}
		}

		for (StructureNode structure : sortedStructures) {
			ExecuteResult result = structure.postLoad(loadContext);
			if (result instanceof ExecuteResult.Failure) {
				return result;
			}
		}

		return ExecuteResult.success();
	}

	/**
	 * Unloads all structures in a section.
	 * @param section The section to unload
	 * @param loadContext The context to unload in
	 */
	public static void unloadStructuresIn(SectionNode section, ExecuteContext loadContext) {
		List<StructureNode> structures = section.children().stream()
			.map(node -> node instanceof StructureNode ? (StructureNode) node : null)
			.toList();

		if (structures.contains(null)) throw new IllegalStateException("Section contains non-structure nodes");

		List<StructureNode> sortedStructures = structures.stream()
			.sorted(Comparator.comparing(StructureNode::priority).reversed())
			.toList();

		for (StructureNode structure : sortedStructures) {
			structure.unload(loadContext);
		}
	}
}
