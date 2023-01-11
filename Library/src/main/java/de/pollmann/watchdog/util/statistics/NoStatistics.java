package de.pollmann.watchdog.util.statistics;

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
