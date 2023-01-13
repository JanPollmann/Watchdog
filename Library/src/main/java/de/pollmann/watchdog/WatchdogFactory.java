package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.tasks.WatchableWithInput;
import de.pollmann.watchdog.util.statistics.NoStatistics;
import de.pollmann.watchdog.util.statistics.StatisticsIntern;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class WatchdogFactory implements WatchdogFactoryExtension {

  private final WatchdogWorker worker;
  private final StatisticsIntern noStatistics = new NoStatistics();

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

  /**
   * Create the default thread factory <br>
   * - Every thread is a daemon thread {@link Thread#setDaemon(boolean)} <br>
   * - Every thread is named 'threadPrefix:{@link System#currentTimeMillis()}' <br>
   *
   * @param threadPrefix name prefix
   * @return a default thread factory
   */
  public static ThreadFactory createDefaultThreadFactory(String threadPrefix) {
    Objects.requireNonNull(threadPrefix);
    return r -> {
      Thread thread = new Thread(r, String.format("%s:%s", threadPrefix, System.currentTimeMillis()));
      thread.setDaemon(true);
      return thread;
    };
  }

  @Override
  public <IN, OUT> RepeatableTaskWithInput<IN, OUT> createRepeated(WatchableOptions watchableOptions, WatchableWithInput<IN, OUT> watchable) {
    return RepeatableTaskWithInput.create(worker, watchableOptions, watchable);
  }

  @Override
  public <OUT> RepeatableTaskWithoutInput<OUT> createRepeated(WatchableOptions watchableOptions, Watchable<OUT> watchable) {
    return RepeatableTaskWithoutInput.create(worker, watchableOptions, watchable);
  }

  @Override
  public Future<?> submitFunctionCall(WatchableOptions watchableOptions, Watchable<?> watchable) {
    return worker.submitFunctionCall(watchableOptions, watchable, noStatistics);
  }

  @Override
  public <OUT> TaskResult<OUT> waitForCompletion(WatchableOptions watchableOptions, Watchable<OUT> watchable) throws InterruptedException {
    return worker.waitForCompletion(watchableOptions, watchable, noStatistics);
  }

}
