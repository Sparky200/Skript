package org.skriptlang.skript.stdlib;

import org.skriptlang.skript.api.SkriptParser;
import org.skriptlang.skript.stdlib.effects.*;
import org.skriptlang.skript.stdlib.expressions.*;
import org.skriptlang.skript.stdlib.structures.CommandStructure;
import org.skriptlang.skript.stdlib.structures.OnScriptLoadEvent;

public final class SyntaxManifest {
	private SyntaxManifest() {
		// no instance
	}

	public static void applySyntax(SkriptParser parser) {
		parser.submitScope(CommandStructure.SCOPE);
		parser.submitNode(CommandStructure.TYPE);
		parser.submitNode(BroadcastEffect.TYPE);
		parser.submitNode(StringLiteralExpression.TYPE);
		parser.submitNode(NumberLiteralExpression.TYPE);
		parser.submitNode(PropertyExpression.TYPE);
		parser.submitScope(OnScriptLoadEvent.SCOPE);
		parser.submitNode(OnScriptLoadEvent.TYPE);
		parser.submitNode(VariableExpression.TYPE);
		parser.submitNode(SetEffect.TYPE);
		parser.submitNode(IncrementEffect.TYPE);
		parser.submitNode(DecrementEffect.TYPE);
		parser.submitNode(AddEffect.TYPE);
		parser.submitNode(RemoveEffect.TYPE);
		parser.submitNode(ListExpression.TYPE);
	}

}
