package de.pollmann.watchdog;

import de.pollmann.watchdog.util.statistics.DefaultStatistics;
import de.pollmann.watchdog.util.statistics.NoStatistics;
import de.pollmann.watchdog.util.statistics.Statistics;
import de.pollmann.watchdog.util.statistics.StatisticsIntern;

import java.util.Objects;

abstract class RepeatableTask implements Statistics {

  protected final StatisticsIntern statistics;
  protected final WatchableOptions watchableOptions;

  private WatchdogWorker worker;

  public RepeatableTask(WatchdogWorker worker, WatchableOptions watchableOptions) {
    this.worker = Objects.requireNonNull(worker);
    this.watchableOptions = watchableOptions;
    if (watchableOptions.isMonitoringEnabled()) {
      statistics = new DefaultStatistics();
    } else {
      statistics = new NoStatistics();
    }
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

  @Override
  public final double getCallsPerSecond() {
    return statistics.getCallsPerSecond();
  }

  protected final WatchdogWorker getWorkerIfAvailable() {
    if (isTerminated()) {
      throw new RepeatableTaskTerminatedException(this);
    }
    return worker;
  }

}
