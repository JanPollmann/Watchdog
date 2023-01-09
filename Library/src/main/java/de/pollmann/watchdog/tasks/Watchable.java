package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

@FunctionalInterface
public interface Watchable<OUT> extends Callable<OUT> {

  default Consumer<TaskResult<OUT>> getResultConsumer() {
    return null;
  }

}
