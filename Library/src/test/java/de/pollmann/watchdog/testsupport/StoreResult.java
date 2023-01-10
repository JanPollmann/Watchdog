package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.tasks.ResultConsumer;

public interface StoreResult<OUT> extends ResultConsumer<OUT> {
  TaskResult<OUT> getLastResult();
  int getFinishedCounter();
}
