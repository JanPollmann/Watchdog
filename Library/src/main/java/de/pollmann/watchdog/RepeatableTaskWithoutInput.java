package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.util.WatchdogUtils;

import java.util.Objects;
import java.util.concurrent.Future;

public class RepeatableTaskWithoutInput<OUT> extends RepeatableTask {

  private final Watchable<OUT> repeated;

  private RepeatableTaskWithoutInput(WatchdogWorker worker, WatchableOptions watchableOptions, Watchable<OUT> repeated) {
    super(worker, watchableOptions);
    this.repeated = Objects.requireNonNull(repeated);
  }

  static <OUT> RepeatableTaskWithoutInput<OUT> create(WatchdogWorker worker, WatchableOptions watchableOptions, Watchable<OUT> watchable) {
    WatchdogUtils.throwExceptionIfInputRequired(watchable, "Cannot build a RepeatableTaskWithoutInput for a watchable with input.");
    return new RepeatableTaskWithoutInput<>(worker, watchableOptions, watchable);
  }

  public Future<?> submitFunctionCall() {
    return getWorkerIfAvailable().submitFunctionCall(watchableOptions, repeated.copy().build(), this);
  }

  public TaskResult<OUT> waitForCompletion() throws InterruptedException {
    return getWorkerIfAvailable().waitForCompletion(watchableOptions, repeated.copy().build(), this);
  }
}
