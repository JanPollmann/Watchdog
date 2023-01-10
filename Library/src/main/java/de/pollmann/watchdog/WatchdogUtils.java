package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.tasks.WatchableWithInput;

public final class WatchdogUtils {

  private WatchdogUtils() {
    throw new RuntimeException("Utility class");
  }

  public static void throwExceptionIfInputRequired(Watchable<?> watchable, String message) {
    if (watchable instanceof WatchableWithInput) {
      throw new IllegalArgumentException(message);
    }
  }

}
