package org.skriptlang.skript.test;

import org.skriptlang.skript.api.*;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.script.StringScriptSource;
import org.skriptlang.skript.api.util.ResultWithDiagnostics;
import org.skriptlang.skript.api.util.ScriptDiagnostic;
import org.skriptlang.skript.parser.LockAccess;
import org.skriptlang.skript.parser.SkriptParserImpl;
import org.skriptlang.skript.stdlib.SyntaxManifest;

public class TestParser {
	public static void main(String[] args) {
		LockAccess lockAccess = new LockAccess();
		SkriptParser parser = new SkriptParserImpl(lockAccess);

		SyntaxManifest.applySyntax(parser);

		lockAccess.lock();

		for (int i = 0; i < 100; i++) {
			long start = System.nanoTime();

			ResultWithDiagnostics<SyntaxNode> result = parser.parse(new StringScriptSource(
				"test.sk",
				"""
					command /test:
						trigger:
							broadcast "Hello, world!"
					
					on script load:
						broadcast name of event-script
					
					"""
			));

			if (result.isSuccess()) {
				System.out.println("Successfully parsed script!");
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
