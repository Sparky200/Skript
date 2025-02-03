package org.skriptlang.skript.engine;

import org.skriptlang.skript.api.SkriptParser;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.util.LockAccess;
import org.skriptlang.skript.parser.SkriptParserImpl;
import org.skriptlang.skript.runtime.SkriptRuntimeImpl;

public class SkriptEngine {
	private final LockAccess lockAccess = new LockAccess();

	private final SkriptParser addonStubParser;
	private final SkriptParser parser = new SkriptParserImpl(lockAccess);
	private final SkriptRuntime runtime = new SkriptRuntimeImpl(lockAccess);

	public SkriptEngine() {
		LockAccess stubLockAccess = new LockAccess();

		this.addonStubParser = new SkriptParserImpl(stubLockAccess);

		stubLockAccess.lock();
	}


}
