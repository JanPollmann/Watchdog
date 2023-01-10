package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

abstract class WatchableWithResultConsumer<OUT> implements Watchable<OUT> {

  protected final ResultConsumer<OUT> resultConsumer;

  protected WatchableWithResultConsumer(WatchableBuilder<?, OUT, ?, ?> builder) {
    if (builder.resultConsumer == null) {
      this.resultConsumer = WatchableWithResultConsumer::emptyConsumer;
    } else {
      this.resultConsumer = builder.resultConsumer;
    }
  }

  @Override
  public final void taskFinished(TaskResult<OUT> taskResult) {
    resultConsumer.accept(taskResult);
  }

  private static void emptyConsumer(TaskResult<?> taskResult) {

  }

}
