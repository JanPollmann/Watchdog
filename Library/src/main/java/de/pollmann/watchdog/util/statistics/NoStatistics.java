package de.pollmann.watchdog.util.statistics;

import de.pollmann.watchdog.WatchableOptions;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.Watchable;

/**
 *  Do nothing implementation for {@link WatchdogFactory#waitForCompletion(WatchableOptions, Watchable)} or {@link WatchdogFactory#submitFunctionCall(WatchableOptions, Watchable)}
 */
public class NoStatistics implements StatisticsIntern {

  private static final NoMemento NO_MEMENTO = new NoMemento();

  @Override
  public TimestampProvider.TimestampSetter initialize() {
    return NO_MEMENTO;
  }

  @Override
  public void finished(TimestampProvider.TimestampSetter state) {

  }

  @Override
  public double getCallsPerSecond() {
    return 0;
  }

  @Override
  public double getAverageApproximatedCallTime() {
    return 0;
  }

  @Override
  public double getAverageApproximatedResultConsumingTime() {
    return 0;
  }

  @Override
  public double getAverageTime() {
    return 0;
  }

}
