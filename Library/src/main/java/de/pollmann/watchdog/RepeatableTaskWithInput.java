package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.WatchableWithInput;

import java.util.Objects;
import java.util.concurrent.Future;

public class RepeatableTaskWithInput<IN, OUT> extends RepeatableTask {

  private final WatchableWithInput<IN, OUT> repeated;
  private final long timeoutInMilliseconds;

  private RepeatableTaskWithInput(WatchdogWorker worker, long timeoutInMilliseconds, WatchableWithInput<IN, OUT> repeated, boolean monitored) {
    super(worker, monitored);
    this.repeated = Objects.requireNonNull(repeated);
    this.timeoutInMilliseconds = timeoutInMilliseconds;
  }

  static <IN, OUT> RepeatableTaskWithInput<IN,OUT> create(WatchdogWorker worker, long timeoutInMilliseconds, WatchableWithInput<IN, OUT> watchable, boolean monitored) {
    return new RepeatableTaskWithInput<>(worker, timeoutInMilliseconds, watchable, monitored);
  }

  public Future<?> submitFunctionCall(IN input) {
    return getWorkerIfAvailable().submitFunctionCall(timeoutInMilliseconds, repeated.newInput(input), this);
  }

  public TaskResult<OUT> waitForCompletion(IN input) throws InterruptedException {
    return getWorkerIfAvailable().waitForCompletion(timeoutInMilliseconds, repeated.newInput(input), this);
  }
}
