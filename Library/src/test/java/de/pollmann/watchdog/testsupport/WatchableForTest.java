package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.TaskResult;

abstract class WatchableForTest<T> {
  private TaskResult<T> lastResult = null;

  public void finishedWithResult(TaskResult<T> result) {
    lastResult = result;
  }

  public TaskResult<T> getLastResult() {
    return lastResult;
  }
}
