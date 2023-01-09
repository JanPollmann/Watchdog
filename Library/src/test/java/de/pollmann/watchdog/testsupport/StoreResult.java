package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.TaskResult;

public interface StoreResult<OUT> {
  TaskResult<OUT> getLastResult();
  int getFinishedCounter();
}
