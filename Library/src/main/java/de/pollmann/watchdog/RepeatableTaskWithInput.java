package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.*;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.Future;

public class RepeatableTaskWithInput<IN, OUT> extends RepeatableTask {

  private final WatchableFunction<IN, OUT> repeated;
  private final long timeoutInMilliseconds;

  private RepeatableTaskWithInput(WatchdogWorker worker, long timeoutInMilliseconds, WatchableFunction<IN, OUT> repeated) {
    super(worker);
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
    return getWorkerIfAvailable().submitFunctionCall(timeoutInMilliseconds, repeated.clone(input));
  }

  public TaskResult<OUT> waitForCompletion(IN input) {
    return getWorkerIfAvailable().waitForCompletion(timeoutInMilliseconds, repeated.clone(input));
  }
}
