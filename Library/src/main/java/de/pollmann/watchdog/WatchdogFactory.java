package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.tasks.WatchableWithInput;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

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

  public <IN, OUT> RepeatableTaskWithInput<IN, OUT> createRepeated(long timeoutInMilliseconds, WatchableWithInput<IN, OUT> watchable) {
    return RepeatableTaskWithInput.create(worker, timeoutInMilliseconds, watchable);
  }

  public <OUT> RepeatableTaskWithoutInput<OUT> createRepeated(long timeoutInMilliseconds, Watchable<OUT> watchable) {
    return RepeatableTaskWithoutInput.create(worker, timeoutInMilliseconds, watchable);
  }

  public Future<?> submitFunctionCall(long timeoutInMilliseconds, Watchable<?> watchable) {
    return worker.submitFunctionCall(timeoutInMilliseconds, watchable);
  }

  public <OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Watchable<OUT> watchable) {
    return worker.waitForCompletion(timeoutInMilliseconds, watchable);
  }

}
