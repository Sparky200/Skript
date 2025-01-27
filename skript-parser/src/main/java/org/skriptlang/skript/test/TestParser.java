package org.skriptlang.skript.test;

import org.skriptlang.skript.api.*;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.SkriptRuntime;
import org.skriptlang.skript.api.script.ScriptSource;
import org.skriptlang.skript.api.script.StringScriptSource;
import org.skriptlang.skript.api.types.*;
import org.skriptlang.skript.api.util.ResultWithDiagnostics;
import org.skriptlang.skript.api.util.ScriptDiagnostic;
import org.skriptlang.skript.parser.LockAccess;
import org.skriptlang.skript.parser.SkriptParserImpl;
import org.skriptlang.skript.runtime.ScriptImpl;
import org.skriptlang.skript.runtime.SkriptRuntimeImpl;
import org.skriptlang.skript.stdlib.SyntaxManifest;

public class TestParser {
	public static void main(String[] args) {
		LockAccess lockAccess = new LockAccess();
		SkriptParser parser = new SkriptParserImpl(lockAccess);

		SyntaxManifest.applySyntax(parser);

		lockAccess.lock();

		for (int i = 0; i < 100; i++) {
			long start = System.nanoTime();

			ScriptSource source = new StringScriptSource(
				"test.sk",
				"""
					on script load:
						broadcast name of event-script
						broadcast length of name of event-script
						broadcast event-script
					
					"""
			);

			ResultWithDiagnostics<SectionNode> result = parser.parse(source);

			if (result.isSuccess()) {
				System.out.println("Successfully parsed script!");

				LockAccess runtimeLockAccess = new LockAccess();
				SkriptRuntime runtime = new SkriptRuntimeImpl(runtimeLockAccess);

				runtime.addType(StringValue.TYPE);
				runtime.addType(NumberValue.TYPE);
				runtime.addType(IterableValue.TYPE);
				runtime.addType(ListValue.TYPE);
				runtime.addType(ErrorValue.TYPE);
				runtime.addType(ScriptInfoValue.TYPE);

				runtime.load(new ScriptImpl(source, result.get()));

			} else {
				System.out.println("Failed to parse script: ");
				for (ScriptDiagnostic diagnostic : result.getDiagnostics()) {
					System.out.println(diagnostic);
				}
			}

			System.out.println("Took " + (System.nanoTime() - start) / 1_000_000 + "ms (" + (System.nanoTime() - start) + ")");
		}

	}
}
