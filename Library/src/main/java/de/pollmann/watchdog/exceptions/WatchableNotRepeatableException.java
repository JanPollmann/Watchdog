package de.pollmann.watchdog.exceptions;

import de.pollmann.watchdog.tasks.Watchable;

public class WatchableNotRepeatableException extends Exception {
  public WatchableNotRepeatableException(Watchable<?> watchable) {
    super(String.format("Repeated call to Watchable [%s] detected. Please use a repeatable task!", watchable));
  }
}
