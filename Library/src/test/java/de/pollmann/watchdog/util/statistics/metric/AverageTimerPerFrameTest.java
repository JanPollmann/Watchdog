package de.pollmann.watchdog.util.statistics.metric;

import de.pollmann.watchdog.util.statistics.TimestampProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

public class AverageTimerPerFrameTest {

  private static final int HISTORY_SIZE = 10;

  @Test
  void nothingHappens_averageIs0() {
    AverageTime averageTime = new AverageTime(HISTORY_SIZE);
    Assertions.assertEquals(0, averageTime.get());
    for (int i = 0; i < 20; i++) {
      averageTime.frameFinished(i % 10, 0);
      Assertions.assertEquals(0, averageTime.get());
    }
  }

  @Test
  void callDoesNotFinish_timeComparesWithEnd() {
    AverageCallTime callTime = new AverageCallTime(HISTORY_SIZE);
    Assertions.assertEquals(100, callTime.calculateTime(new TimestampProviderForTest() {
      @Override
      public Long getBeginCall() {
        return 0L;
      }

      @Override
      public Long getEnd() {
        return 100L;
      }
    }));
  }

  @Test
  void callFinish_timeComparesWithStopCall() {
    AverageCallTime callTime = new AverageCallTime(HISTORY_SIZE);
    Assertions.assertEquals(50, callTime.calculateTime(new TimestampProviderForTest() {
      @Override
      public Long getBeginCall() {
        return 0L;
      }

      @Override
      public Long getStopCall() {
        return 50L;
      }

      @Override
      public Long getEnd() {
        return 100L;
      }
    }));
  }

  @Test
  void callHasNotStarted_timeIs0() {
    AverageCallTime callTime = new AverageCallTime(HISTORY_SIZE);
    Assertions.assertEquals(0, callTime.calculateTime(new TimestampProviderForTest() {
      @Override
      public Long getStopCall() {
        return 50L;
      }

      @Override
      public Long getEnd() {
        return 100L;
      }
    }));
  }

  @Test
  void resultConsumingDoesNotFinish_timeComparesWithEnd() {
    AverageResultConsumingTime time = new AverageResultConsumingTime(HISTORY_SIZE);
    Assertions.assertEquals(100, time.calculateTime(new TimestampProviderForTest() {
      @Override
      public Long getBeginResultConsuming() {
        return 0L;
      }

      @Override
      public Long getEnd() {
        return 100L;
      }
    }));
  }

  @Test
  void resultConsumingFinish_timeComparesWithResultConsuming() {
    AverageResultConsumingTime time = new AverageResultConsumingTime(HISTORY_SIZE);
    Assertions.assertEquals(50, time.calculateTime(new TimestampProviderForTest() {
      @Override
      public Long getBeginResultConsuming() {
        return 0L;
      }

      @Override
      public Long getStopResultConsuming() {
        return 50L;
      }

      @Override
      public Long getEnd() {
        return 100L;
      }
    }));
  }

  @Test
  void resultConsumingHasNotStarted_timeIs0() {
    AverageResultConsumingTime time = new AverageResultConsumingTime(HISTORY_SIZE);
    Assertions.assertEquals(0, time.calculateTime(new TimestampProviderForTest() {
      @Override
      public Long getStopResultConsuming() {
        return 50L;
      }

      @Override
      public Long getEnd() {
        return 100L;
      }
    }));
  }

  @Test
  void onePerFrameWith100ms_averageIs100() {
    AverageTime averageTime = new AverageTime(HISTORY_SIZE);
    loop(20, i -> {
      averageTime.mementoFinished(new TimestampProviderForTest(){
        @Override
        public Long getStart() {
          return i;
        }

        @Override
        public Long getEnd() {
          return i + 100;
        }
      });
      averageTime.frameFinished((int) i.longValue() % 10, 1);
    }, i -> {
      Assertions.assertEquals(100, averageTime.get());
    });
  }


  private void loop(int repeats, Consumer<Long> toLoop, Consumer<Long> toTest) {
    long i = 0;
    // fill data arrays
    for (; i < HISTORY_SIZE; i++) {
      toLoop.accept(i);
    }
    // test
    for (; i < HISTORY_SIZE + repeats; i++) {
      toLoop.accept(i);
      toTest.accept(i);
    }
  }

  private static class TimestampProviderForTest implements TimestampProvider {

    @Override
    public Long getStart() {
      return null;
    }

    @Override
    public Long getEnd() {
      return null;
    }

    @Override
    public Long getBeginCall() {
      return null;
    }

    @Override
    public Long getStopCall() {
      return null;
    }

    @Override
    public Long getBeginResultConsuming() {
      return null;
    }

    @Override
    public Long getStopResultConsuming() {
      return null;
    }
  }

}
