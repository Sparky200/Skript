package org.skriptlang.skript.runtime;

import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.api.nodes.SectionNode;
import org.skriptlang.skript.api.script.Script;
import org.skriptlang.skript.api.script.ScriptSource;

public record ScriptImpl(@NotNull ScriptSource source, @NotNull SectionNode root) implements Script { }
