package org.skriptlang.skript.stdlib.structures;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.entries.StructureEntryNode;
import org.skriptlang.skript.api.nodes.StructureNode;
import org.skriptlang.skript.api.nodes.StructureNodeType;
import org.skriptlang.skript.api.nodes.TokenNode;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.ErrorValue;
import org.skriptlang.skript.api.types.RuntimeSkriptType;
import org.skriptlang.skript.api.types.StructValue;
import org.skriptlang.skript.api.types.base.SkriptTypeFactory;
import org.skriptlang.skript.api.util.ExecuteResult;

import java.util.Objects;

import static org.skriptlang.skript.api.entries.EntryStructureDefinition.entryStructure;
import static org.skriptlang.skript.api.nodes.NodeTypeBuilders.structure;
import static org.skriptlang.skript.api.types.base.SkriptTypeFactory.skriptType;

/**
 * The structure to create in-language structs with.
 * These are backed by a class that directly extends SkriptValue called {@link StructValue}.
 * This syntax node will create a new special skript type alongside the value,
 * where the StructValue will be given a reference to the type.
 */
public final class StructStructure implements StructureNode {
	public static final StructureNodeType<StructStructure> TYPE = structure(StructStructure.class)
		.syntaxes(
			"define [a] [new] struct <token::identifier>:<entries>"
		)
		.structure(entryStructure()
			.fallback("<token::identifier>: <token::identifier>", context -> context.token(0).tokenContents())
			.build())
		.create(context -> new StructStructure(
			context.token(0),
			context.entries()
		))
		.build();

	private final TokenNode nameResolver;
	private final StructureEntryNode[] properties;

	public StructStructure(
		TokenNode nameResolver,
		StructureEntryNode[] properties
	) {
		this.nameResolver = nameResolver;
		this.properties = properties;
	}

	public record StructScriptData(@NotNull RuntimeSkriptType<StructValue> runtimeType) {}

	@Override
	public @NotNull ExecuteResult load(@NotNull ExecuteContext context) {
		SkriptTypeFactory<StructValue> typeBuilder = skriptType(nameResolver.tokenContents(), StructValue.class)
			.runtimeFactory((runtime, source, superType, properties1) ->
				new StructValue.StructType(runtime, Objects.requireNonNull(context.script()), source, properties1));

		for (StructureEntryNode property : properties) {
			String typeName = ((TokenNode) property.children().getLast()).tokenContents();
			RuntimeSkriptType<?> propertyType = context.runtime().getTypeByName(typeName);
			if (propertyType == null) {
				return ExecuteResult.failure(new ErrorValue(this, "Type " + typeName + " does not exist"));
			}

			typeBuilder.property(
				property.name(),
				runtime ->
					new StructValue.StructType.Property(runtime, propertyType.valueClass())
			);
		}

		RuntimeSkriptType<StructValue> runtimeType = typeBuilder.build().construct(context.runtime());
		Objects.requireNonNull(context.scriptContext()).addType(runtimeType);

		context.addScriptData(this, new StructScriptData(runtimeType));

		return StructureNode.super.load(context);
	}

	@Override
	public @NotNull ExecuteResult unload(@NotNull ExecuteContext context) {
		Object scriptData = context.getScriptData(this);
		if (!(scriptData instanceof StructScriptData(RuntimeSkriptType<StructValue> runtimeType)))
			return StructureNode.super.unload(context);

		context.removeScriptData(this);
		Objects.requireNonNull(context.scriptContext()).removeType(runtimeType);

		return StructureNode.super.unload(context);
	}

}
