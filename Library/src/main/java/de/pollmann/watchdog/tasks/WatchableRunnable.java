package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

public interface WatchableRunnable extends InterruptableRunnable, WatchableCallable<Object> {
  void finishedWithResult(TaskResult<Object> result);

  @Override
  default Object call() throws Exception {
    run();
    return null;
  }
}
