package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.tasks.WatchableCallable;
import de.pollmann.watchdog.tasks.WatchableRunnable;

import java.util.concurrent.*;

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
    return r -> {
      Thread thread = new Thread(r, String.format("%s:%s", threadPrefix, System.currentTimeMillis()));
      thread.setDaemon(true);
      return thread;
    };
  }

  public <T> Future<?> submitFunctionCall(long timeoutInMilliseconds, Watchable<T> callable) {
    return worker.submitFunctionCall(timeoutInMilliseconds, callable);
  }

  public Future<?> submitFunctionCall(long timeoutInMilliseconds, WatchableRunnable runnable) {
    return worker.submitFunctionCall(timeoutInMilliseconds, runnable);
  }

  public <T> TaskResult<T> waitForCompletion(long timeoutInMilliseconds, Watchable<T> callable) {
    return worker.waitForCompletion(timeoutInMilliseconds, callable);
  }

  public TaskResult<?> waitForCompletion(long timeoutInMilliseconds, WatchableRunnable runnable) {
    return worker.waitForCompletion(timeoutInMilliseconds, runnable);
  }

}
