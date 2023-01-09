package de.pollmann.watchdog.tasks;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface WatchableCallable<OUT> extends Callable<OUT>, Watchable<OUT> {
}
