package de.pollmann.watchdog.util.statistics;

/**
 * Statistics for repeated tasks only.
 */
public interface Statistics {

  /**
   * @return the calls per second
   */
  double getCallsPerSecond();
}
