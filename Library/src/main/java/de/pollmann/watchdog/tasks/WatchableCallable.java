package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

import java.util.concurrent.Callable;

public interface WatchableCallable<T> extends Callable<T> {
  void finishedWithResult(TaskResult<T> result);
}
