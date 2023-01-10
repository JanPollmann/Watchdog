package de.pollmann.watchdog.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WatchableTest {

  @Test
  void buildRunnableWithInput_throwsException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Watchable.builder(() -> {}).withInput(new Object()));
  }

  @Test
  void buildCallableWithInput_throwsException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Watchable.builder(() -> null).withInput(new Object()));
  }

}
