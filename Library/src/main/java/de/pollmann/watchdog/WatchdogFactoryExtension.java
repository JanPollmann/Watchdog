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
   * Create a repeatable task with input and default options
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable the watchable to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @see #createRepeated(WatchableOptions, WatchableWithInput) for more options
   */
  default <IN, OUT> RepeatableTaskWithInput<IN, OUT> createRepeated(long timeoutInMilliseconds, WatchableWithInput<IN, OUT> watchable) {
    return createRepeated(WatchableOptions.builder(timeoutInMilliseconds).build(), watchable);
  }

  /**
   * Create a repeatable task without input and default options
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable watchable the watchable to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @throws IllegalArgumentException if the watchable requires input
   * @see #createRepeated(WatchableOptions, Watchable) for more options
   */
  default  <OUT> RepeatableTaskWithoutInput<OUT> createRepeated(long timeoutInMilliseconds, Watchable<OUT> watchable) {
    return createRepeated(WatchableOptions.builder(timeoutInMilliseconds).build(), watchable);
  }

  /**
   * Submit the watchable for execution with default options
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable watchable the watchable to invoke
   * @return the future resulting from {@link ExecutorService#submit(java.util.concurrent.Callable)}}
   * @see #submitFunctionCall(WatchableOptions, Watchable) for more options
   */
  default Future<?> submitFunctionCall(long timeoutInMilliseconds, Watchable<?> watchable) {
    return submitFunctionCall(WatchableOptions.builder(timeoutInMilliseconds).build(), watchable);
  }

  /**
   * Call the watchable directly (blocking call) with default options
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable the watchable to invoke
   * @param <OUT> the output type
   * @return the task result
   * @throws InterruptedException if the thread gets interrupted
   * @see #waitForCompletion(WatchableOptions, Watchable) for more options
   */
  default  <OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Watchable<OUT> watchable) throws InterruptedException {
    return waitForCompletion(WatchableOptions.builder(timeoutInMilliseconds).build(), watchable);
  }

}
