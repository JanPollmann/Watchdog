package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.tasks.WatchableRunnable;
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

  public Future<?> submitFunctionCall(long timeoutInMilliseconds, Watchable<?> callable) {
    return worker.submitFunctionCall(timeoutInMilliseconds, callable);
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

  public <IN> TaskResult<?> waitForCompletion(long timeoutInMilliseconds, Consumer<IN> consumer, IN data) {
    return waitForCompletion(timeoutInMilliseconds, new WrappedWatchableConsumer<>(consumer, data));
  }

  public <IN,OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Function<IN,OUT> function, IN data) {
    return waitForCompletion(timeoutInMilliseconds, new WrappedWatchableFunction<>(function, data));
  }

}
