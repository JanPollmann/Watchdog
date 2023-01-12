package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

@FunctionalInterface
public interface ResultConsumer<OUT> {
  void accept(TaskResult<OUT> t) throws InterruptedException;
}
