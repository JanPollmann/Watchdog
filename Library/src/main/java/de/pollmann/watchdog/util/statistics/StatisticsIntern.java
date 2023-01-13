package de.pollmann.watchdog.util.statistics;

/**
 * Internal statistics API
 */
public interface StatisticsIntern extends Statistics {
  TimestampProvider.TimestampSetter initialize();
  void finished(TimestampProvider.TimestampSetter state);
}
