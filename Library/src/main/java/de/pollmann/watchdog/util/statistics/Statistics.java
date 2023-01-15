package de.pollmann.watchdog.util.statistics;

import de.pollmann.watchdog.WatchableOptions;

/**
 * Statistics for repeated tasks only. Please notice: A garbage collection will strongly influence small execution durations!<br>
 * By default, the statistics are disabled! To activate them {@link WatchableOptions.Builder#enableStatistics()} must be called
 *
 * @see #getCallsPerSecond() calls per second
 * @see #getAverageApproximatedCallTime() average execution duration of the function call
 * @see #getAverageApproximatedResultConsumingTime() average  execution duration of the result consuming function
 * @see #getAverageApproximatedUserTime() average execution duration of the "not library" code
 * @see #getAverageTime() average execution duration of the whole call (with overhead from the library)
 * @see #getAverageApproximatedOverhead() average overhead from the library
 * @see #getRelativeAverageApproximatedOverhead() the overhead as relative as a relative metric to {@link #getAverageTime}
 */
public interface Statistics {

  /**
   * @return the calls per second
   */
  double getCallsPerSecond();

  /**
   * This metric is influenced from garbage collections!
   *
   * @return the average call time in ns
   */
  double getAverageApproximatedCallTime();

  /**
   * This metric is influenced from garbage collections!
   *
   * @return the average result consuming time in ns
   */
  double getAverageApproximatedResultConsumingTime();

  /**
   * This metric is influenced from garbage collections!
   *
   * @return the sum of {@link #getAverageApproximatedCallTime()} and {@link #getAverageApproximatedResultConsumingTime()} in ns
   */
  default double getAverageApproximatedUserTime() {
    return getAverageApproximatedCallTime() + getAverageApproximatedResultConsumingTime();
  }

  /**
   * This metric is influenced from garbage collections!
   *
   * @return the overall computation time in ns (user + overhead)
   */
  double getAverageTime();

  /**
   * This metric is influenced from garbage collections!
   *
   * @return the average overhead in ns
   */
  default double getAverageApproximatedOverhead() {
    return getAverageTime() - getAverageApproximatedUserTime();
  }

  /**
   * This metric is influenced from garbage collections!
   *
   * @return the average overhead
   */
  default double getRelativeAverageApproximatedOverhead() {
    double averageTime = getAverageTime();
    if (averageTime == 0) {
      return 0;
    } else {
      return getAverageApproximatedOverhead() / averageTime;
    }
  }
}
