package de.pollmann.watchdog.tasks;

public abstract class WatchableFunction<IN, OUT> implements Watchable<OUT> {

  private final IN input;

  public WatchableFunction(IN input) {
    this.input = input;
  }

  @Override
  public OUT call() throws Exception {
    return apply(input);
  }

  public abstract OUT apply(IN input) throws Exception;

  public abstract WatchableFunction<IN, OUT> clone(IN newInput);
}
