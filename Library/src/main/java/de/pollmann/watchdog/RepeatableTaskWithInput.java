package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.WatchableWithInput;

import java.util.Objects;
import java.util.concurrent.Future;

public class RepeatableTaskWithInput<IN, OUT> extends RepeatableTask {

  private final WatchableWithInput<IN, OUT> repeated;


  private RepeatableTaskWithInput(WatchdogWorker worker, WatchableOptions watchableOptions, WatchableWithInput<IN, OUT> repeated) {
    super(worker, watchableOptions);
    this.repeated = Objects.requireNonNull(repeated);
  }

  static <IN, OUT> RepeatableTaskWithInput<IN,OUT> create(WatchdogWorker worker, WatchableOptions watchableOptions, WatchableWithInput<IN, OUT> watchable) {
    return new RepeatableTaskWithInput<>(worker, watchableOptions, watchable);
  }

  public Future<?> submitFunctionCall(IN input) {
    return getWorkerIfAvailable().submitFunctionCall(watchableOptions, repeated.newInput(input).build(), statistics);
  }

  public TaskResult<OUT> waitForCompletion(IN input) throws InterruptedException {
    return getWorkerIfAvailable().waitForCompletion(watchableOptions, repeated.newInput(input).build(), statistics);
  }
}
