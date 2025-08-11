package org.skriptlang.skript.stdlib.structures;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.entries.EntryStructureDefinition;
import org.skriptlang.skript.api.entries.StructureEntryNode;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.scope.InputDefinition;
import org.skriptlang.skript.api.scope.SectionScope;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.SectionUtils;

import java.util.List;
import java.util.Map;

import static org.skriptlang.skript.api.entries.EntryStructureDefinition.entryStructure;
import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.structure;

public record CommandStructure(
	@NotNull String name,
	SectionNode trigger,
	@NotNull StringNode description,
	@Nullable StringNode prefix
) implements StructureNode {

	public static final SectionScope SCOPE = new SectionScope("command", List.of(
		new InputDefinition("command", "command_data")
	));

	public static final StructureNodeType<CommandStructure> TYPE = structure(CommandStructure.class)
		.syntaxes("command /<token::identifier>:<entries>")
		.structure(entryStructure()
			// these strings could be tokenized the same way as syntaxes
			.entry("trigger", "trigger:<section::command>")
			.entry("description", "description:<token::string>", true)
			.entry("prefix", "prefix:<token::string>", true)
			.build()
		)
		.create((children, matchedPattern, entries) -> {
			Preconditions.checkNotNull(entries, "Commands contractually expect entries");

			String name = ((TokenNode) children.getFirst()).tokenContents();

			StructureEntryNode trigger = entries.get("trigger");
			if (trigger == null) {
				throw new IllegalStateException("No trigger entry found");
			}
			SectionNode triggerSection = (SectionNode) trigger.children().getFirst();

			StructureEntryNode descriptionNode = entries.get("description");
			StringNode description = descriptionNode == null ? null : (StringNode) descriptionNode.children().getFirst();

			StructureEntryNode prefixNode = entries.get("prefix");
			StringNode prefix = prefixNode == null ? null : (StringNode) prefixNode.children().getFirst();

			return new CommandStructure(
				name,
				triggerSection,
				description,
				prefix
			);
		})
		.build();

	@Override
	public @NotNull ExecuteResult load(@NotNull ExecuteContext context) {
		// instead of new Object, this would be something like "new CommandData(...)"
		// either way, use name, description, prefix, and trigger to create the object

		// contextForTrigger just makes sure we aren't "leaking" the file context to other places.
		// it's really just a formality
		ExecuteContext contextForTrigger = context.fork();

		context.addScriptData(this, contextForTrigger);
		return ExecuteResult.success();
	}

	@Override
	public @NotNull ExecuteResult postLoad(@NotNull ExecuteContext context) {
		// sync commands
		// (object would be CommandData)
		List<Object> commands = context.getAllScriptData().entrySet().stream()
			.filter(entry -> entry.getKey() instanceof CommandStructure)
			.map(Map.Entry::getValue)
			.toList();

		// TODO: send to synchronizer which would probably be a stateful singleton somewhere

		return ExecuteResult.success();
	}

	@Override
	public @NotNull ExecuteResult unload(@NotNull ExecuteContext context) {
		context.removeScriptData(this);
		return ExecuteResult.success();
	}

	private @NotNull ExecuteResult executeCommand(@NotNull ExecuteContext context, @NotNull Object event) {
		ExecuteContext baseTriggerContext = context.getScriptData(this, ExecuteContext.class);
		if (baseTriggerContext == null) {
			return ExecuteResult.failure(new ErrorValue("Command cannot be executed because it previously failed to load"));
		}
		ExecuteContext thisTrigger = baseTriggerContext.fork();

//		thisTrigger.setLiteralVariable("command", event.getCommandSender());

		return SectionUtils.executeSimple(trigger, thisTrigger);
	}
}
