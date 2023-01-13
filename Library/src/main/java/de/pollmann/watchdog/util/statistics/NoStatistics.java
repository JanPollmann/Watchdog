package de.pollmann.watchdog.util.statistics;

import de.pollmann.watchdog.WatchableOptions;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.Watchable;

/**
 *  Do nothing implementation for {@link WatchdogFactory#waitForCompletion(WatchableOptions, Watchable)} or {@link WatchdogFactory#submitFunctionCall(WatchableOptions, Watchable)}
 */
public class NoStatistics implements StatisticsIntern {

  @Override
  public Memento initialize() {
    return null;
  }

  @Override
  public void beginCall(Memento state) {

  }

  @Override
  public void stopCall(Memento state) {

  }

  @Override
  public void beginResultConsuming(Memento state) {

  }

  @Override
  public void stopResultConsuming(Memento state) {

  }

  @Override
  public void finished(Memento state) {

  }

  @Override
  public double getCallsPerSecond() {
    return 0;
  }

}
