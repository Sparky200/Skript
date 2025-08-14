package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.nodes.SyntaxNode;

import static org.skriptlang.skript.api.types.base.SkriptTypeFactory.skriptType;

/**
 * Represents an error that Skript error handling may also utilize.
 */
public class ErrorValue extends SkriptValue {
	public static final SkriptType<ErrorValue> TYPE = skriptType("error", ErrorValue.class)
		.build();

	private final @Nullable SyntaxNode source;
	private final StringValue message;

	/**
	 * Creates a new error value.
	 * <p>
	 * The source should be provided whenever possible, or null if the source is impossible to determine.
	 * @param source The source of the error, or null if unknown.
	 * @param message The message of the error.
	 */
	public ErrorValue(@Nullable SyntaxNode source, StringValue message) {
		this.source = source;
		this.message = message;
	}

	/**
	 * Creates a new error value.
	 * <p>
	 * The source should be provided whenever possible, or null if the source is impossible to determine.
	 * As such, it's recommended to use {@link #ErrorValue(SyntaxNode, StringValue)} instead.
	 * @param message The message of the error.
	 */
	public ErrorValue(StringValue message) {
		this(null, message);
	}

	/**
	 * Creates a new error value.
	 * <p>
	 * The source should be provided whenever possible, or null if the source is impossible to determine.
	 * @param source The source of the error, or null if unknown.
	 * @param message The message of the error.
	 */
	public ErrorValue(@Nullable SyntaxNode source, String message) {
		this(source, new StringValue(message));
	}

	/**
	 * Creates a new error value.
	 * <p>
	 * The source should be provided whenever possible, or null if the source is impossible to determine.
	 * As such, it's recommended to use {@link #ErrorValue(SyntaxNode, String)} instead.
	 * @param message The message of the error.
	 */
	public ErrorValue(String message) {
		this(new StringValue(message));
	}

	public @Nullable SyntaxNode source() {
		return source;
	}

	public StringValue message() {
		return message;
	}

	public static @NotNull ErrorValue of(Throwable throwable) {
		return new ErrorValue(throwable.getMessage());
	}

	@Override
	public String toString() {
		return message.toString();
	}
}
