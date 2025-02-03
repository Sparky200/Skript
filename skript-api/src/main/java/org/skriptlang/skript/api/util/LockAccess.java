package org.skriptlang.skript.api.util;

import org.jetbrains.annotations.ApiStatus;

/**
 * A class that allows delegating lock control to an orchestrator.
 */
@ApiStatus.Internal
public final class LockAccess {
	private volatile boolean locked = false;

	public void lock() {
		locked = true;
	}

	public void unlock() {
		locked = false;
	}

	public boolean isLocked() {
		return locked;
	}
}
