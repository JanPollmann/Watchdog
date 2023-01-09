package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.*;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class WatchdogFactory {

  private final WatchdogWorker worker;

  public WatchdogFactory(ExecutorService watchdogPool, ExecutorService workerPool) {
    worker = new WatchdogWorker(watchdogPool, workerPool);
  }

  public WatchdogFactory(String threadPrefix) {
    this(
      Executors.newFixedThreadPool(2, createDefaultThreadFactory(String.format("%s:watchdog", threadPrefix))),
      Executors.newFixedThreadPool(2, createDefaultThreadFactory(String.format("%s:worker", threadPrefix)))
    );
  }

  public WatchdogFactory() {
    this(WatchdogFactory.class.getSimpleName());
  }

  public static ThreadFactory createDefaultThreadFactory(String threadPrefix) {
    Objects.requireNonNull(threadPrefix);
    return r -> {
      Thread thread = new Thread(r, String.format("%s:%s", threadPrefix, System.currentTimeMillis()));
      thread.setDaemon(true);
      return thread;
    };
  }

  public <OUT> RepeatableTaskWithoutInput<OUT> createRepeated(long timeoutInMilliseconds, WatchableCallable<OUT> callable) {
    return RepeatableTaskWithoutInput.create(worker, timeoutInMilliseconds, callable);
  }

  public RepeatableTaskWithoutInput<Object> createRepeated(long timeoutInMilliseconds, WatchableRunnable runnable) {
    return RepeatableTaskWithoutInput.create(worker, timeoutInMilliseconds, runnable);
  }

  public <IN, OUT> RepeatableTaskWithInput<IN, OUT> createRepeated(long timeoutInMilliseconds, WatchableFunction<IN, OUT> function) {
    return RepeatableTaskWithInput.create(worker, timeoutInMilliseconds, function);
  }

  public <IN> RepeatableTaskWithInput<IN, Object> createRepeated(long timeoutInMilliseconds, WatchableConsumer<IN> consumer) {
    return RepeatableTaskWithInput.create(worker, timeoutInMilliseconds, consumer);
  }

  public Future<?> submitFunctionCall(long timeoutInMilliseconds, Watchable<?> watchable) {
    return worker.submitFunctionCall(timeoutInMilliseconds, watchable);
  }

  public Future<?> submitFunctionCall(long timeoutInMilliseconds, WatchableRunnable runnable) {
    return worker.submitFunctionCall(timeoutInMilliseconds, runnable);
  }

  public TaskResult<?> waitForCompletion(long timeoutInMilliseconds, WatchableRunnable runnable) {
    return worker.waitForCompletion(timeoutInMilliseconds, runnable);
  }

  public <OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Watchable<OUT> callable) {
    return worker.waitForCompletion(timeoutInMilliseconds, callable);
  }

  public <IN> TaskResult<?> waitForCompletion(long timeoutInMilliseconds, Consumer<TaskResult<Object>> resultConsumer, Consumer<IN> consumer, IN data) {
    return waitForCompletion(timeoutInMilliseconds, new WatchableConsumer<>(resultConsumer, consumer, data));
  }

  public <IN,OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Consumer<TaskResult<OUT>> resultConsumer, Function<IN,OUT> function, IN data) {
    return waitForCompletion(timeoutInMilliseconds, new WatchableFunction<>(resultConsumer, function, data));
  }

  public <IN> TaskResult<?> waitForCompletion(long timeoutInMilliseconds, Consumer<IN> consumer, IN data) {
    return waitForCompletion(timeoutInMilliseconds, new WatchableConsumer<>(consumer, data));
  }

  public <IN,OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Function<IN,OUT> function, IN data) {
    return waitForCompletion(timeoutInMilliseconds, new WatchableFunction<>(function, data));
  }

}
