package de.pollmann.watchdog.util.statistics;

import de.pollmann.watchdog.WatchableOptions;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.Watchable;

/**
 *  Do nothing implementation for {@link WatchdogFactory#waitForCompletion(WatchableOptions, Watchable)} or {@link WatchdogFactory#submitFunctionCall(WatchableOptions, Watchable)}
 */
public class NoStatistics implements Statistics {
  @Override
  public Memento beginCall() {
    return null;
  }

  @Override
  public void stopCall(Memento state) {

  }

  @Override
  public double getCallsPerSecond() {
    return 0;
  }
}
