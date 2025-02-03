package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.NotNull;

import static org.skriptlang.skript.api.types.base.SkriptValueTypeFactory.skriptType;

public class ErrorValue extends SkriptValue {
	public static final StagedSkriptValueType<ErrorValue> TYPE = skriptType("error", ErrorValue.class)
		.build();

	private final StringValue message;

	public ErrorValue(StringValue message) {
		this.message = message;
	}

	public ErrorValue(String message) {
		this(new StringValue(message));
	}

	public StringValue message() {
		return message;
	}

	public static @NotNull ErrorValue of(Throwable throwable) {
		return new ErrorValue(throwable.getMessage());
	}
}
