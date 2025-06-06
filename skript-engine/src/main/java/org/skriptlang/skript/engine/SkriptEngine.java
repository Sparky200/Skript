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

public class SkriptEngine {
	private final LockAccess lockAccess = new LockAccess();

	private final SkriptParser addonStubParser;
	private final SkriptParser parser = new SkriptParserImpl(lockAccess);
	private final SkriptRuntime runtime = new SkriptRuntimeImpl(lockAccess);

	public SkriptEngine(Consumer<SkriptParser> tempRegistrar) {
		LockAccess stubLockAccess = new LockAccess();

		this.addonStubParser = new SkriptParserImpl(stubLockAccess);

		stubLockAccess.lock();
		tempRegistrar.accept(this.parser);
		lockAccess.lock();
	}

	public ExecuteContext eval(ScriptSource source) {
		long start = System.nanoTime();
		var parseResult = parser.parse(source);
		System.out.println("Parse time: " + ((System.nanoTime() - start) / 1000000.0) + "ms");
		if (!parseResult.isSuccess()) throw new RuntimeException("Failed to parse script");

		var script = new ScriptImpl(source, parseResult.get());
		return runtime.load(script);
	}

}
