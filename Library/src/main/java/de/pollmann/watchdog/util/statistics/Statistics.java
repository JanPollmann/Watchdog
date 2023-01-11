package de.pollmann.watchdog.util.statistics;

public interface Statistics {
  Memento beginCall();
  void stopCall(Memento state);
  double getCallsPerSecond();
}
