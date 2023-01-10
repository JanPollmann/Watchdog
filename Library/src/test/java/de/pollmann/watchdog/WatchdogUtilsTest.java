package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WatchdogUtilsTest {

  @Test
  void throwExceptionIfInputRequired_works() {
    Watchable<?> watchable = Watchable.builder(in -> in).build();

    Assertions.assertThrows(IllegalArgumentException.class, () -> WatchdogUtils.throwExceptionIfInputRequired(watchable, ""));
  }

}
