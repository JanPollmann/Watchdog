package de.pollmann.watchdog.exceptions;

import de.pollmann.watchdog.tasks.Watchable;

public class WatchableInIncorrectState extends Exception {
  public WatchableInIncorrectState(Watchable<?> watchable) {
    super(String.format("The Watchable [%s] is in an incorrect state!", watchable));
  }
}
