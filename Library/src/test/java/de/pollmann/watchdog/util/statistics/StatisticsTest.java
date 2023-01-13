package de.pollmann.watchdog.util.statistics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StatisticsTest {

  @Test
  void getRelativeAverageApproximatedOverhead() {
    Assertions.assertEquals(0.5, new Statistics() {
      @Override
      public double getCallsPerSecond() {
        return 0;
      }

      @Override
      public double getAverageApproximatedCallTime() {
        return 6;
      }

      @Override
      public double getAverageApproximatedResultConsumingTime() {
        return 4;
      }

      @Override
      public double getAverageTime() {
        return 20;
      }
    }.getRelativeAverageApproximatedOverhead());
  }

  @Test
  void getRelativeAverageApproximatedOverhead_noException() {
    Assertions.assertEquals(0, new Statistics() {
      @Override
      public double getCallsPerSecond() {
        return 0;
      }

      @Override
      public double getAverageApproximatedCallTime() {
        return 0;
      }

      @Override
      public double getAverageApproximatedResultConsumingTime() {
        return 0;
      }

      @Override
      public double getAverageTime() {
        return 0;
      }
    }.getRelativeAverageApproximatedOverhead());
  }

  @Test
  void getAverageApproximatedOverhead() {
    Assertions.assertEquals(5, new Statistics() {
      @Override
      public double getCallsPerSecond() {
        return 0;
      }

      @Override
      public double getAverageApproximatedCallTime() {
        return 6;
      }

      @Override
      public double getAverageApproximatedResultConsumingTime() {
        return 4;
      }

      @Override
      public double getAverageTime() {
        return 15;
      }
    }.getAverageApproximatedOverhead());
  }

  @Test
  void getAverageApproximatedUserTime() {
    Assertions.assertEquals(42, new Statistics() {
      @Override
      public double getCallsPerSecond() {
        return 0;
      }

      @Override
      public double getAverageApproximatedCallTime() {
        return 40;
      }

      @Override
      public double getAverageApproximatedResultConsumingTime() {
        return 2;
      }

      @Override
      public double getAverageTime() {
        return 0;
      }
    }.getAverageApproximatedUserTime());
  }
}
