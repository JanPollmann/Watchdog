package de.pollmann.watchdog.util.statistics;

import java.util.Objects;

public class DelegatingStatistics implements Statistics {

  private final Statistics statistics;

  protected DelegatingStatistics(Statistics statistics) {
    this.statistics = Objects.requireNonNull(statistics);
  }

  @Override
  public double getCallsPerSecond() {
    return statistics.getCallsPerSecond();
  }

  @Override
  public double getAverageApproximatedCallTime() {
    return statistics.getAverageApproximatedCallTime();
  }

  @Override
  public double getAverageApproximatedResultConsumingTime() {
    return statistics.getAverageApproximatedResultConsumingTime();
  }

  @Override
  public double getAverageTime() {
    return statistics.getAverageTime();
  }
}
