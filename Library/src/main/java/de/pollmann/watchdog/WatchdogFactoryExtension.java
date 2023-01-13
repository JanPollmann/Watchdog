package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.tasks.WatchableWithInput;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

interface WatchdogFactoryExtension {

  /**
   * Create a repeated task with input
   *
   * @param watchableOptions the function call options
   * @param watchable the task to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @see #createRepeated(WatchableOptions, Watchable) for tasks without input
   */
  <IN, OUT> RepeatableTaskWithInput<IN, OUT> createRepeated(WatchableOptions watchableOptions, WatchableWithInput<IN, OUT> watchable);

  /**
   * Create a repeated task without input
   *
   * @param watchableOptions the function call options
   * @param watchable the task to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @throws IllegalArgumentException if the watchable requires input
   * @see #createRepeated(WatchableOptions, WatchableWithInput) for tasks with input
   */
  <OUT> RepeatableTaskWithoutInput<OUT> createRepeated(WatchableOptions watchableOptions, Watchable<OUT> watchable);

  /**
   * Submit the watchable to an executor service
   *
   * @param watchableOptions the function call options
   * @param watchable watchable the task to invoke
   * @return the future resulting from {@link ExecutorService#submit(java.util.concurrent.Callable)}}
   */
  Future<?> submitFunctionCall(WatchableOptions watchableOptions, Watchable<?> watchable);

  /**
   * Call the watchable directly (blocking call)
   *
   * @param watchableOptions the function call options
   * @param watchable the task to invoke
   * @param <OUT> the output type
   * @return the task result
   * @throws InterruptedException if the thread gets interrupted
   */
  <OUT> TaskResult<OUT> waitForCompletion(WatchableOptions watchableOptions, Watchable<OUT> watchable) throws InterruptedException;

  /**
   * Create a repeatable task with input and with disabled statistics
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable the watchable to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @deprecated use {@link #createRepeated(WatchableOptions, WatchableWithInput)} instead
   */
  @Deprecated
  default <IN, OUT> RepeatableTaskWithInput<IN, OUT> createRepeated(long timeoutInMilliseconds, WatchableWithInput<IN, OUT> watchable) {
    return createRepeated(WatchableOptions.builder(timeoutInMilliseconds).build(), watchable);
  }

  /**
   * Create a repeatable task with input
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param withStatistics enable/disable statistics
   * @param watchable the watchable to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @deprecated use {@link #createRepeated(WatchableOptions, WatchableWithInput)} instead
   */
  @Deprecated
  default <IN, OUT> RepeatableTaskWithInput<IN, OUT> createRepeated(long timeoutInMilliseconds, boolean withStatistics, WatchableWithInput<IN, OUT> watchable) {
    WatchableOptions.Builder builder = WatchableOptions.builder(timeoutInMilliseconds);
    if (withStatistics) {
      builder.enableStatistics();
    }
    return createRepeated(builder.build(), watchable);
  }

  /**
   * Create a repeatable task without input and with disabled statistics
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable watchable the watchable to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @throws IllegalArgumentException if the watchable requires input
   * @deprecated use {@link #createRepeated(WatchableOptions, Watchable)} instead
   */
  @Deprecated
  default  <OUT> RepeatableTaskWithoutInput<OUT> createRepeated(long timeoutInMilliseconds, Watchable<OUT> watchable) {
    return createRepeated(WatchableOptions.builder(timeoutInMilliseconds).build(), watchable);
  }

  /**
   * Create a repeatable task without input
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param withStatistics enable/disable statistics
   * @param watchable the watchable to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @throws IllegalArgumentException if the watchable requires input
   * @deprecated use {@link #createRepeated(WatchableOptions, Watchable)} instead
   */
  @Deprecated
  default  <OUT> RepeatableTaskWithoutInput<OUT> createRepeated(long timeoutInMilliseconds, boolean withStatistics, Watchable<OUT> watchable) {
    WatchableOptions.Builder builder = WatchableOptions.builder(timeoutInMilliseconds);
    if (withStatistics) {
      builder.enableStatistics();
    }
    return createRepeated(builder.build(), watchable);
  }

  /**
   * Submit the watchable to an executor service
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable watchable the watchable to invoke
   * @return the future resulting from {@link ExecutorService#submit(java.util.concurrent.Callable)}}
   * @deprecated use {@link #submitFunctionCall(WatchableOptions, Watchable)} instead
   */
  @Deprecated
  default Future<?> submitFunctionCall(long timeoutInMilliseconds, Watchable<?> watchable) {
    return submitFunctionCall(WatchableOptions.builder(timeoutInMilliseconds).build(), watchable);
  }

  /**
   * Call the watchable directly (blocking call)
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable the watchable to invoke
   * @param <OUT> the output type
   * @return the task result
   * @throws InterruptedException if the thread gets interrupted
   * @deprecated use {@link #waitForCompletion(WatchableOptions, Watchable)} instead
   */
  @Deprecated
  default  <OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Watchable<OUT> watchable) throws InterruptedException {
    return waitForCompletion(WatchableOptions.builder(timeoutInMilliseconds).build(), watchable);
  }

}
