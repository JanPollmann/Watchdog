package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.InterruptableRunnable;
import de.pollmann.watchdog.tasks.WatchableCallable;

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

  public <T> Future<?> submitFunctionCall(long timeoutInMilliseconds, WatchableCallable<T> callable) {
    return worker.submitFunctionCall(timeoutInMilliseconds, callable);
  }

  public <T> TaskResult<T> waitForCompletion(long timeoutInMilliseconds, Callable<T> callable) {
    return worker.waitForCompletion(timeoutInMilliseconds, callable);
  }

  public TaskResult<?> waitForCompletion(long timeoutInMilliseconds, InterruptableRunnable runnable) {
    return worker.waitForCompletion(timeoutInMilliseconds, () -> {
      runnable.run();
      return null;
    });
  }

}
