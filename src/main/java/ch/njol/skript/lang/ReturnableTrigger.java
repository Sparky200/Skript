package ch.njol.skript.lang;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.script.Script;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ReturnableTrigger<T> extends Trigger implements ReturnHandler<T> {

	private final ReturnHandler<T> handler;

	public ReturnableTrigger(ReturnHandler<T> handler, @Nullable Script script, String name, SkriptEvent event, Function<ReturnHandler<T>, List<TriggerItem>> loadItems) {
		super(script, name, event, Collections.emptyList());
		this.handler = handler;
		setTriggerItems(loadItems.apply(this));
	}

	@Override
	public void returnValues(Event event, Expression<? extends T> value) {
		handler.returnValues(event, value);
	}

	@Override
	public boolean isSingleReturnValue() {
		return handler.isSingleReturnValue();
	}

	@Override
	public @Nullable Class<? extends T> returnValueType() {
		return handler.returnValueType();
	}

}
