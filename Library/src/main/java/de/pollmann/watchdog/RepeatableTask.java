package de.pollmann.watchdog;

import de.pollmann.watchdog.util.statistics.DefaultStatistics;
import de.pollmann.watchdog.util.statistics.DelegatingStatistics;
import de.pollmann.watchdog.util.statistics.NoStatistics;
import de.pollmann.watchdog.util.statistics.StatisticsIntern;

import java.util.Objects;

abstract class RepeatableTask extends DelegatingStatistics {

  protected final StatisticsIntern statistics;
  protected final WatchableOptions watchableOptions;

  private WatchdogWorker worker;

  private RepeatableTask(StatisticsIntern statistics, WatchdogWorker worker, WatchableOptions watchableOptions) {
    super(statistics);
    this.worker = Objects.requireNonNull(worker);
    this.watchableOptions = Objects.requireNonNull(watchableOptions);
    this.statistics = Objects.requireNonNull(statistics);
  }

  public RepeatableTask(WatchdogWorker worker, WatchableOptions watchableOptions) {
    this(watchableOptions.isMonitoringEnabled() ? new DefaultStatistics() : new NoStatistics(), worker, watchableOptions);
  }

  public final void terminate() {
    this.worker = null;
  }

  public final boolean isTerminated() {
    if (worker == null) {
      return true;
    } else if (worker.isTerminated()){
      terminate();
      return true;
    }
    return false;
  }

  protected final WatchdogWorker getWorkerIfAvailable() {
    if (isTerminated()) {
      throw new RepeatableTaskTerminatedException(this);
    }
    return worker;
  }

}
