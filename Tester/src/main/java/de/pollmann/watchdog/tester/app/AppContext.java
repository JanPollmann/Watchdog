package de.pollmann.watchdog.tester.app;

import de.pollmann.watchdog.WatchdogFactory;

public class AppContext {
  private final WatchdogFactory watchdogFactory;
  private final long loopTimeout;

  public AppContext(long loopTimeout) {
    this.loopTimeout = loopTimeout;
    watchdogFactory = new WatchdogFactory("context");
  }

  public AppContext() {
    this(0);
  }

  public WatchdogFactory getWatchdogFactory() {
    return watchdogFactory;
  }

  public long getLoopTimeout() {
    return loopTimeout;
  }
}
