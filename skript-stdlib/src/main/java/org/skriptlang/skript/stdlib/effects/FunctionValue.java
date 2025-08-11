package org.skriptlang.skript.stdlib.effects;

import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.types.SkriptValue;
import org.skriptlang.skript.api.types.SkriptType;
import org.skriptlang.skript.api.util.ExecuteResult;

import static org.skriptlang.skript.api.types.base.SkriptTypeFactory.skriptType;

public class FunctionValue extends SkriptValue {
	public static final SkriptType<FunctionValue> TYPE = skriptType("function", FunctionValue.class)
		.build();

	@FunctionalInterface
	public interface Adapter {
		/**
		 * @param calleeContext The context from the callee.
		 * @param args The arguments for the function.
		 * @apiNote It is not correct behavior to return {@link ExecuteResult.Success} from this function
		 * @return Either a {@link ExecuteResult.Returning} on success or {@link ExecuteResult.Failure} on fail.
		 * 		   The return type should be NoneValue if no return value exists.
		 * 		   If a success result is returned, it should be adapted into a NoneValue.
		 */
		ExecuteResult run(ExecuteContext calleeContext, SkriptValue[] args);
	}

	private final Adapter value;

	public FunctionValue(Adapter adapter) {
		this.value = adapter;
	}

	public ExecuteResult run(ExecuteContext calleeContext, SkriptValue[] args) {
		return value.run(calleeContext, args);
	}
}
