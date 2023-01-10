package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

import java.util.concurrent.Callable;

public interface Watchable<OUT> extends Callable<OUT> {
  void taskFinished(TaskResult<OUT> taskResult);

  static WatchableBuilder<Object, Object, ExceptionRunnable, Watchable<Object>> builder(ExceptionRunnable task) {
    return WatchableRunnable.builder(task);
  }

  static <OUT> WatchableBuilder<Object, OUT, Callable<OUT>, Watchable<OUT>> builder(Callable<OUT> task) {
    return WatchableCallable.builder(task);
  }

  static <IN> WatchableBuilder<IN, Object, ExceptionConsumer<IN>, WatchableWithInput<IN, Object>> builder(ExceptionConsumer<IN> task) {
    return WatchableConsumer.builder(task);
  }

  static <IN, OUT> WatchableBuilder<IN, OUT, ExceptionFunction<IN, OUT>, WatchableWithInput<IN, OUT>> builder(ExceptionFunction<IN, OUT> task) {
    return WatchableFunction.builder(task);
  }
}
