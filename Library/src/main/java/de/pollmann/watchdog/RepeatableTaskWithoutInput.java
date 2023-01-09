package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.tasks.WatchableCallable;
import de.pollmann.watchdog.tasks.WatchableRunnable;

import java.util.Objects;
import java.util.concurrent.Future;

public class RepeatableTaskWithoutInput<OUT> {

  private final Watchable<OUT> repeated;
  private final WatchdogWorker worker;
  private final long timeoutInMilliseconds;

  private RepeatableTaskWithoutInput(WatchdogWorker worker, long timeoutInMilliseconds, Watchable<OUT> repeated) {
    this.worker = Objects.requireNonNull(worker);
    this.repeated = Objects.requireNonNull(repeated);
    this.timeoutInMilliseconds = timeoutInMilliseconds;
  }

  static RepeatableTaskWithoutInput<Object> create(WatchdogWorker worker, long timeoutInMilliseconds, WatchableRunnable repeated) {
    return new RepeatableTaskWithoutInput<>(worker, timeoutInMilliseconds, repeated);
  }

  static <OUT> RepeatableTaskWithoutInput<OUT> create(WatchdogWorker worker, long timeoutInMilliseconds, WatchableCallable<OUT> repeated) {
    return new RepeatableTaskWithoutInput<>(worker, timeoutInMilliseconds, repeated);
  }

  public Future<?> submitFunctionCall() {
    return worker.submitFunctionCall(timeoutInMilliseconds, repeated);
  }

  public TaskResult<OUT> waitForCompletion() {
    return worker.waitForCompletion(timeoutInMilliseconds, repeated);
  }
}
