package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.*;

import java.util.Objects;
import java.util.concurrent.Future;

class RepeatableTaskWithInput<IN, OUT> {

  private final WatchableFunction<IN, OUT> repeated;
  private final WatchdogWorker worker;
  private final long timeoutInMilliseconds;

  private RepeatableTaskWithInput(WatchdogWorker worker, long timeoutInMilliseconds, WatchableFunction<IN, OUT> repeated) {
    this.worker = Objects.requireNonNull(worker);
    this.repeated = Objects.requireNonNull(repeated);
    this.timeoutInMilliseconds = timeoutInMilliseconds;
  }

  static <IN> RepeatableTaskWithInput<IN,Object> create(WatchdogWorker worker, long timeoutInMilliseconds, WatchableConsumer<IN> repeated) {
    return new RepeatableTaskWithInput<>(worker, timeoutInMilliseconds, repeated);
  }

  static <IN, OUT> RepeatableTaskWithInput<IN,OUT> create(WatchdogWorker worker, long timeoutInMilliseconds, WatchableFunction<IN, OUT> repeated) {
    return new RepeatableTaskWithInput<>(worker, timeoutInMilliseconds, repeated);
  }

  public Future<?> submitFunctionCall(IN input) {
    return worker.submitFunctionCall(timeoutInMilliseconds, repeated);
  }

  public TaskResult<OUT> waitForCompletion(IN input) {
    return worker.waitForCompletion(timeoutInMilliseconds, repeated);
  }
}
