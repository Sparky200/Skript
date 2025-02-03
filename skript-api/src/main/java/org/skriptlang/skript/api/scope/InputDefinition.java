package org.skriptlang.skript.api.scope;

import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.types.SkriptValue;

/**
 * An input definition in a scope or syntax.
 */
public record InputDefinition(String name, String type) {
	public InputDefinition(String name, @Nullable String type) {
		this.name = name;
		this.type = type == null ? SkriptValue.TYPE.typeName() : type;
	}
}
