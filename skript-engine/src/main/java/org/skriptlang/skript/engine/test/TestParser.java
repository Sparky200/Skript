package org.skriptlang.skript.engine.test;

import org.skriptlang.skript.api.runtime.ScriptContext;
import org.skriptlang.skript.api.script.FileScriptSource;
import org.skriptlang.skript.api.util.ResultWithDiagnostics;
import org.skriptlang.skript.engine.SkriptEngine;
import org.skriptlang.skript.stdlib.SyntaxManifest;

import java.nio.file.Path;

public class TestParser {
	public static void main(String[] args) {

		SkriptEngine engine = new SkriptEngine(SyntaxManifest::applySyntax);

		for (int i = 0; i < 1000; i++) {
			long start = System.nanoTime();
			ResultWithDiagnostics<ScriptContext> result = engine.eval(new FileScriptSource(Path.of("beans.sk")));

			if (!result.isSuccess()) {
				System.out.println("Failed to parse script");
				for (var diagnostic : result.getDiagnostics())
					System.out.println(diagnostic);
				return;
			}

			System.out.println("Took " + ((System.nanoTime() - start) / 1000000.0) + "ms");
		}

	}
}
