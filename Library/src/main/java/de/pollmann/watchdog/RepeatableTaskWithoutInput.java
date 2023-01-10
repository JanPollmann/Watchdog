package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;

import java.util.Objects;
import java.util.concurrent.Future;

public class RepeatableTaskWithoutInput<OUT> extends RepeatableTask {

  private final Watchable<OUT> repeated;
  private final long timeoutInMilliseconds;

  private RepeatableTaskWithoutInput(WatchdogWorker worker, long timeoutInMilliseconds, Watchable<OUT> repeated) {
    super(worker);
    this.repeated = Objects.requireNonNull(repeated);
    this.timeoutInMilliseconds = timeoutInMilliseconds;
  }

  static <OUT> RepeatableTaskWithoutInput<OUT> create(WatchdogWorker worker, long timeoutInMilliseconds, Watchable<OUT> watchable) {
    WatchdogUtils.throwExceptionIfInputRequired(watchable, "Cannot build a RepeatableTaskWithoutInput for a watchable with input.");
    return new RepeatableTaskWithoutInput<>(worker, timeoutInMilliseconds, watchable);
  }

  public Future<?> submitFunctionCall() {
    return getWorkerIfAvailable().submitFunctionCall(timeoutInMilliseconds, repeated);
  }

  public TaskResult<OUT> waitForCompletion() {
    return getWorkerIfAvailable().waitForCompletion(timeoutInMilliseconds, repeated);
  }
}
