package org.skriptlang.skript.api.types;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.runtime.SkriptRuntime;

import java.util.Map;

/**
 * RuntimeSkriptTypeFactory is an optional, transformative function to produce a runtime-valid {@link SkriptType}.
 * If not provided, the default runtime type will be a direct instance of
 * {@link org.skriptlang.skript.api.types.base.RuntimeSkriptTypeBase RuntimeSkriptTypeBase}.
 * @param <T>
 */
@FunctionalInterface
public interface RuntimeSkriptTypeFactory<T extends SkriptValue> {
	RuntimeSkriptType<T> construct(
		@NotNull SkriptRuntime runtime,
		@NotNull SkriptType<T> source,
		@NotNull RuntimeSkriptType<? super T> superType,
		@NotNull Map<String, RuntimeSkriptProperty<? super T, ?>> properties
	);
}
