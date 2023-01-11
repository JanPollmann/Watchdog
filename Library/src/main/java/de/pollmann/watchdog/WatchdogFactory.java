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

  public WatchdogFactory(ExecutorService watchdogPool, ExecutorService workerPool) {
    worker = new WatchdogWorker(watchdogPool, workerPool);
  }

  public WatchdogFactory(String threadPrefix, int numberOfWatchdogsAndWorker) {
    this(
      Executors.newFixedThreadPool(numberOfWatchdogsAndWorker, createDefaultThreadFactory(String.format("%s:watchdog", threadPrefix))),
      Executors.newFixedThreadPool(numberOfWatchdogsAndWorker, createDefaultThreadFactory(String.format("%s:worker", threadPrefix)))
    );
  }

  public WatchdogFactory(int numberOfWatchdogsAndWorker) {
    this(WatchdogFactory.class.getSimpleName(), numberOfWatchdogsAndWorker);
  }

  public WatchdogFactory(String threadPrefix) {
    this(threadPrefix, 2);
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

  public <IN, OUT> RepeatableTaskWithInput<IN, OUT> createRepeated(long timeoutInMilliseconds, WatchableWithInput<IN, OUT> watchable) {
    return createRepeated(timeoutInMilliseconds, false, watchable);
  }

  public <OUT> RepeatableTaskWithoutInput<OUT> createRepeated(long timeoutInMilliseconds, Watchable<OUT> watchable) {
    return createRepeated(timeoutInMilliseconds, false, watchable);
  }

  public <IN, OUT> RepeatableTaskWithInput<IN, OUT> createRepeated(long timeoutInMilliseconds, boolean withStatistics, WatchableWithInput<IN, OUT> watchable) {
    return RepeatableTaskWithInput.create(worker, timeoutInMilliseconds, watchable, withStatistics);
  }

  public <OUT> RepeatableTaskWithoutInput<OUT> createRepeated(long timeoutInMilliseconds, boolean withStatistics, Watchable<OUT> watchable) {
    return RepeatableTaskWithoutInput.create(worker, timeoutInMilliseconds, watchable, withStatistics);
  }

  public Future<?> submitFunctionCall(long timeoutInMilliseconds, Watchable<?> watchable) {
    return worker.submitFunctionCall(timeoutInMilliseconds, watchable, statistics);
  }

  public <OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Watchable<OUT> watchable) throws InterruptedException {
    return worker.waitForCompletion(timeoutInMilliseconds, watchable, statistics);
  }

}
