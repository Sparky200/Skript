package org.skriptlang.skript.api.types;

import org.skriptlang.skript.api.script.ScriptSource;

import static org.skriptlang.skript.api.types.base.SkriptPropertyFactory.skriptProperty;
import static org.skriptlang.skript.api.types.base.SkriptValueTypeFactory.skriptType;

public final class ScriptInfoValue extends SkriptValue {
	public static final StagedSkriptValueType<ScriptInfoValue> TYPE = skriptType("script_info", ScriptInfoValue.class)
		.property("name", skriptProperty(ScriptInfoValue.class, StringValue.class)
			.getter(ScriptInfoValue::name)
		)
		.build();

	private final StringValue name;

	public ScriptInfoValue(StringValue name) {
		this.name = name;
	}

	public StringValue name() {
		return name;
	}

	public static ScriptInfoValue ofSource(ScriptSource source) {
		return new ScriptInfoValue(new StringValue(source.name()));
	}
}
