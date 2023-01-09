package de.pollmann.watchdog.tasks;

public abstract class WatchableFunction<IN, OUT> implements Watchable<OUT> {

  private IN input = null;

  public void setInput(IN input) {
    this.input = input;
  }

  public IN getInput() {
    return input;
  }

  @Override
  public OUT call() throws Exception {
    return apply(getInput());
  }

  public abstract OUT apply(IN input) throws Exception;
}
