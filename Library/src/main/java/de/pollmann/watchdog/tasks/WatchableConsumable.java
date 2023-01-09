package de.pollmann.watchdog.tasks;

public class WatchableConsumable<IN, OUT> implements Watchable<OUT> {

  @Override
  public OUT call() throws Exception {
    return null;
  }
}
