package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.script.Script;

import static org.skriptlang.skript.api.types.base.SkriptPropertyFactory.skriptProperty;
import static org.skriptlang.skript.api.types.base.SkriptValueTypeFactory.skriptType;

public final class ScriptInfoValue extends SkriptValue {
	public static final StagedSkriptValueType<ScriptInfoValue> TYPE = skriptType("script_info", ScriptInfoValue.class)
		.property("name", skriptProperty(ScriptInfoValue.class, StringValue.class)
			.getter(ScriptInfoValue::name)
		)
		.property("length", skriptProperty(ScriptInfoValue.class, NumberValue.class)
			.getter(ScriptInfoValue::sourceLength)
		)
		.build();

	private final StringValue name;
	private final NumberValue sourceLength;

	public ScriptInfoValue(StringValue name, NumberValue sourceLength) {
		this.name = name;
		this.sourceLength = sourceLength;
	}

	public StringValue name() {
		return name;
	}

	public NumberValue sourceLength() {
		return sourceLength;
	}

	public static ScriptInfoValue ofSource(@NotNull Script source) {
		return new ScriptInfoValue(new StringValue(source.source().name()), new NumberValue(source.source().content().length()));
	}
}
