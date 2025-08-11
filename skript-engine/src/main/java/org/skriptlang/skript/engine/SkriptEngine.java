package org.skriptlang.skript.engine;

import org.skriptlang.skript.api.SkriptParser;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.script.ScriptSource;
import org.skriptlang.skript.api.util.LockAccess;
import org.skriptlang.skript.parser.SkriptParserImpl;
import org.skriptlang.skript.runtime.ScriptImpl;
import org.skriptlang.skript.runtime.SkriptRuntimeImpl;

import java.util.function.Consumer;

/**
 * High-level orchestration entry point for parsing and loading Skript scripts.
 * This class wires a parser and runtime together and coordinates locking after
 * syntax registration.
 */
public class SkriptEngine {
	private final LockAccess lockAccess = new LockAccess();

	private final SkriptParser addonStubParser;
	private final SkriptParser parser = new SkriptParserImpl(lockAccess);
	private final SkriptRuntime runtime = new SkriptRuntimeImpl(lockAccess);

	/**
	 * Creates an engine and allows the caller to register syntax on the parser
	 * before locking the system for execution.
	 * @param tempRegistrar a registrar that submits node types and scopes to the parser
	 */
	public SkriptEngine(Consumer<SkriptParser> tempRegistrar) {
		LockAccess stubLockAccess = new LockAccess();

		this.addonStubParser = new SkriptParserImpl(stubLockAccess);

		stubLockAccess.lock();
		tempRegistrar.accept(this.parser);
		lockAccess.lock();
	}

	/**
	 * Parses the provided script source and loads it into the runtime, returning
	 * an ExecuteContext if loading succeeds, or {@code null} if loading fails.
	 * Parsing failures result in an exception.
	 */
	public ExecuteContext eval(ScriptSource source) {
		var parseResult = parser.parse(source);
		if (!parseResult.isSuccess()) throw new RuntimeException("Failed to parse script");

		var script = new ScriptImpl(source, parseResult.get());
		return runtime.load(script);
	}

}
