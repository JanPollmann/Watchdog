package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.tasks.WatchableConsumer;
import de.pollmann.watchdog.tasks.WatchableFunction;
import de.pollmann.watchdog.tasks.WatchableRunnable;

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

  public <IN> Future<?> submitFunctionCall(long timeoutInMilliseconds, WatchableFunction<IN,?> function, IN data) {
    function.setInput(data);
    return worker.submitFunctionCall(timeoutInMilliseconds, function);
  }

  public TaskResult<?> waitForCompletion(long timeoutInMilliseconds, WatchableRunnable runnable) {
    return worker.waitForCompletion(timeoutInMilliseconds, runnable);
  }

  public <OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Watchable<OUT> callable) {
    return worker.waitForCompletion(timeoutInMilliseconds, callable);
  }

  public <IN> TaskResult<?> waitForCompletion(long timeoutInMilliseconds, Consumer<IN> consumer, IN data) {
    return waitForCompletion(timeoutInMilliseconds, new WrappedWatchableConsumer<>(consumer), data);
  }

  public <IN> TaskResult<?> waitForCompletion(long timeoutInMilliseconds, WatchableConsumer<IN> consumer, IN data) {
    consumer.setInput(data);
    return worker.waitForCompletion(timeoutInMilliseconds, consumer);
  }

  public <IN,OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Function<IN,OUT> function, IN data) {
    return waitForCompletion(timeoutInMilliseconds, new WrappedWatchableFunction<>(function), data);
  }

  public <IN,OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, WatchableFunction<IN,OUT> function, IN data) {
    function.setInput(data);
    return worker.waitForCompletion(timeoutInMilliseconds, function);
  }

  private static class WrappedWatchableFunction<IN,OUT> extends WatchableFunction<IN,OUT> {

    private final Function<IN,OUT> function;

    public WrappedWatchableFunction(Function<IN,OUT> function) {
      this.function = function;
    }

    @Override
    public OUT apply(IN input) {
      return function.apply(input);
    }
  }

  private static class WrappedWatchableConsumer<IN> extends WatchableConsumer<IN> {

    private final Consumer<IN> consumer;

    public WrappedWatchableConsumer(Consumer<IN> consumer) {
      this.consumer = consumer;
    }

    @Override
    public void accept(IN input) throws Exception {
      consumer.accept(input);
    }
  }

}
