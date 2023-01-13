package de.pollmann.watchdog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WatchableOptionsTest {

  @Test
  void defaultHasNoStatistics() {
    WatchableOptions options = WatchableOptions.builder(1).build();

    Assertions.assertEquals(1, options.getTimeoutInMilliseconds());
    Assertions.assertFalse(options.isMonitoringEnabled());
  }

  @Test
  void enableAndDisableStatistics_works() {
    WatchableOptions options = WatchableOptions.builder(2).build();

    Assertions.assertEquals(2, options.getTimeoutInMilliseconds());
    Assertions.assertFalse(options.isMonitoringEnabled());

    options = WatchableOptions.builder(3).enableStatistics().build();

    Assertions.assertEquals(3, options.getTimeoutInMilliseconds());
    Assertions.assertTrue(options.isMonitoringEnabled());

    options = WatchableOptions.builder(0).enableStatistics().disableStatistics().build();

    Assertions.assertEquals(0, options.getTimeoutInMilliseconds());
    Assertions.assertFalse(options.isMonitoringEnabled());

    options = WatchableOptions.builder(10000).disableStatistics().enableStatistics().build();

    Assertions.assertEquals(10000, options.getTimeoutInMilliseconds());
    Assertions.assertTrue(options.isMonitoringEnabled());
  }
}
