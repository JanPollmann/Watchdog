package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface Watchable<OUT> extends Callable<OUT> {
  default void finishedWithResult(TaskResult<OUT> result) {

  }
}
