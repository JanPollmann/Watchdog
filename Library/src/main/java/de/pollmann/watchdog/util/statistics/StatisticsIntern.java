package de.pollmann.watchdog.util.statistics;

/**
 * Internal statistics API
 */
public interface StatisticsIntern extends Statistics {
  Memento initialize();
  void beginCall(Memento state);
  void stopCall(Memento state);
  void beginResultConsuming(Memento state);
  void stopResultConsuming(Memento state);
  void finished(Memento state);
}
