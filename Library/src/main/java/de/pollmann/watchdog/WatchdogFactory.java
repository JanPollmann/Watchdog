package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.tasks.WatchableWithInput;
import de.pollmann.watchdog.util.statistics.NoStatistics;
import de.pollmann.watchdog.util.statistics.Statistics;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class WatchdogFactory {

  private final WatchdogWorker worker;
  private final Statistics statistics = new NoStatistics();

  /**
   * Create a factory with custom executor services, please note: numberOfThreads(watchdogPool) >= numberOfThreads(workerPool). The factory worker calls {@link ExecutorService#shutdown()} for both of them in {@link Object#finalize()}
   *
   * @param watchdogPool the watchdog pool
   * @param workerPool the worker pool
   */
  public WatchdogFactory(ExecutorService watchdogPool, ExecutorService workerPool) {
    worker = new WatchdogWorker(watchdogPool, workerPool);
  }

  /**
   * Create a factory with 2 * numberOfWatchdogsAndWorker {@link Thread}s (numberOfWatchdogsAndWorker worker threads)
   *
   * @param threadPrefix the name prefix for every {@link Thread}
   * @param numberOfWatchdogsAndWorker the number of {@link Thread}s per {@link ExecutorService}
   * @see #WatchdogFactory(ExecutorService, ExecutorService)
   */
  public WatchdogFactory(String threadPrefix, int numberOfWatchdogsAndWorker) {
    this(
      Executors.newFixedThreadPool(numberOfWatchdogsAndWorker, createDefaultThreadFactory(String.format("%s:watchdog", threadPrefix))),
      Executors.newFixedThreadPool(numberOfWatchdogsAndWorker, createDefaultThreadFactory(String.format("%s:worker", threadPrefix)))
    );
  }

  /**
   * Create a factory with 2 * numberOfWatchdogsAndWorker {@link Thread}s (numberOfWatchdogsAndWorker worker threads)
   *
   * @param numberOfWatchdogsAndWorker the number of {@link Thread}s per {@link ExecutorService}
   * @see #WatchdogFactory(String, int)
   * @see #WatchdogFactory(ExecutorService, ExecutorService)
   */
  public WatchdogFactory(int numberOfWatchdogsAndWorker) {
    this(WatchdogFactory.class.getSimpleName(), numberOfWatchdogsAndWorker);
  }

  /**
   * Create a factory with 4 {@link Thread}s (2 worker threads)
   *
   * @param threadPrefix the number of {@link Thread}s per {@link ExecutorService}
   * @see #WatchdogFactory(String, int)
   * @see #WatchdogFactory(ExecutorService, ExecutorService)
   */
  public WatchdogFactory(String threadPrefix) {
    this(threadPrefix, 2);
  }

  /**
   * Create a factory with 4 {@link Thread}s (2 worker threads) and the factory classname as thread name prefix for every {@link Thread}
   *
   * @see #WatchdogFactory(String)
   * @see #WatchdogFactory(int)
   * @see #WatchdogFactory(ExecutorService, ExecutorService)
   */
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

  /**
   * Create a repeatable task with input and with disabled statistics
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable watchable the watchable to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @see #createRepeated(long, boolean, WatchableWithInput)
   * @throws IllegalArgumentException if the watchable requires input
   */
  public <IN, OUT> RepeatableTaskWithInput<IN, OUT> createRepeated(long timeoutInMilliseconds, WatchableWithInput<IN, OUT> watchable) {
    return createRepeated(timeoutInMilliseconds, false, watchable);
  }

  /**
   * Create a repeatable task with input
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param withStatistics enable/disable statistics
   * @param watchable watchable the watchable to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @see #createRepeated(long, WatchableWithInput)
   */
  public <IN, OUT> RepeatableTaskWithInput<IN, OUT> createRepeated(long timeoutInMilliseconds, boolean withStatistics, WatchableWithInput<IN, OUT> watchable) {
    return RepeatableTaskWithInput.create(worker, timeoutInMilliseconds, watchable, withStatistics);
  }

  /**
   * Create a repeatable task without input and with disabled statistics
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable watchable the watchable to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @see #createRepeated(long, boolean, Watchable)
   */
  public <OUT> RepeatableTaskWithoutInput<OUT> createRepeated(long timeoutInMilliseconds, Watchable<OUT> watchable) {
    return createRepeated(timeoutInMilliseconds, false, watchable);
  }

  /**
   * Create a repeatable task without input
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param withStatistics enable/disable statistics
   * @param watchable the watchable to invoke
   * @param <OUT> the output type
   * @return a repeatable task
   * @see #createRepeated(long, Watchable)
   * @throws IllegalArgumentException if the watchable requires input
   */
  public <OUT> RepeatableTaskWithoutInput<OUT> createRepeated(long timeoutInMilliseconds, boolean withStatistics, Watchable<OUT> watchable) {
    return RepeatableTaskWithoutInput.create(worker, timeoutInMilliseconds, watchable, withStatistics);
  }

  /**
   * Submit the watchable to an executor service
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable watchable the watchable to invoke
   * @return the future resulting from {@link ExecutorService#submit(java.util.concurrent.Callable)}}
   */
  public Future<?> submitFunctionCall(long timeoutInMilliseconds, Watchable<?> watchable) {
    return worker.submitFunctionCall(timeoutInMilliseconds, watchable, statistics);
  }

  /**
   * Call the watchable directly (blocking call)
   *
   * @param timeoutInMilliseconds the timeout in milliseconds
   * @param watchable the watchable to invoke
   * @param <OUT> the output type
   * @return the task result
   * @throws InterruptedException when the API call to {@link Watchable#stop()} gets interrupted
   */
  public <OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Watchable<OUT> watchable) throws InterruptedException {
    return worker.waitForCompletion(timeoutInMilliseconds, watchable, statistics);
  }

}
