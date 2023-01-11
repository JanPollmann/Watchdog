package de.pollmann.watchdog;

public class WatchableNotRepeatableException extends Exception {
  public WatchableNotRepeatableException() {
    super("Repeated call to Watchable detected. Please use a repeatable task!");
  }
}
