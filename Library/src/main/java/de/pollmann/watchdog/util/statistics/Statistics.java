package de.pollmann.watchdog.util.statistics;

/**
 * Statistics for repeated tasks only.
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
