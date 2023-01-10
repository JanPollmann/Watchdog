package de.pollmann.watchdog.tasks;

public interface WatchableWithInput<IN, OUT> extends Watchable<OUT>, ChangeableInput<IN, OUT> {
}
