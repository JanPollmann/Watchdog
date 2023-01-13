package de.pollmann.watchdog.util.statistics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DelegatingStatisticsTest {

  @Test
  void everythingIsDelegated() {
    validateAll(new DelegationTestClass(5), 5);
    validateAll(new DelegationTestClass(7.2), 7.2);
  }

  private void validateAll(Statistics statistics, double value) {
    DelegatingStatistics delegatingStatistics = new DelegatingStatistics(statistics);
    Assertions.assertEquals(value, delegatingStatistics.getCallsPerSecond());
    Assertions.assertEquals(value, delegatingStatistics.getAverageApproximatedCallTime());
    Assertions.assertEquals(value, delegatingStatistics.getAverageApproximatedResultConsumingTime());
    Assertions.assertEquals(value, delegatingStatistics.getAverageTime());
  }


  private static class DelegationTestClass implements Statistics {

    private double value;

    private DelegationTestClass(double value) {
      this.value = value;
    }

    @Override
    public double getCallsPerSecond() {
      return value;
    }

    @Override
    public double getAverageApproximatedCallTime() {
      return value;
    }

    @Override
    public double getAverageApproximatedResultConsumingTime() {
      return value;
    }

    @Override
    public double getAverageTime() {
      return value;
    }
  }
}
