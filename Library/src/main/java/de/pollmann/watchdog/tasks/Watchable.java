package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

import java.util.concurrent.Callable;

/**
 * The Scheduled function is wrapped in a watchable. The watchable is unique per function call. To repeat the same call, use {@link de.pollmann.watchdog.WatchdogFactory#createRepeated(de.pollmann.watchdog.WatchableOptions, Watchable)}
 *
 * @param <OUT> the output of the function call (if any)
 */
public interface Watchable<OUT> extends Callable<OUT>, Stoppable, Repeatable<OUT> {
  /**
   * This function gets called if the task is finished for any reason.
   *
   * @param taskResult the task result
   * @see WatchableBuilder#withResultConsumer(ResultConsumer) withResultConsumer - delegate the result
   */
  void taskFinished(TaskResult<OUT> taskResult) throws InterruptedException;

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
