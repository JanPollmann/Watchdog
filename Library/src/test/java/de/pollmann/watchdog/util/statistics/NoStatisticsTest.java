package de.pollmann.watchdog.util.statistics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NoStatisticsTest {

  @Test
  void everythingIsZero() {
    NoStatistics noStatistics = new NoStatistics();

    Assertions.assertNotNull(noStatistics.initialize());
    Assertions.assertEquals(0, noStatistics.getAverageApproximatedCallTime());
    Assertions.assertEquals(0, noStatistics.getAverageApproximatedResultConsumingTime());
    Assertions.assertEquals(0, noStatistics.getAverageApproximatedUserTime());
    Assertions.assertEquals(0, noStatistics.getAverageTime());
    Assertions.assertEquals(0, noStatistics.getCallsPerSecond());
    Assertions.assertEquals(0, noStatistics.getAverageApproximatedOverhead());
    Assertions.assertEquals(0, noStatistics.getRelativeAverageApproximatedOverhead());
  }

}
