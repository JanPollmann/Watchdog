package de.pollmann.watchdog.tasks;

public abstract class WatchableConsumer<IN> extends WatchableFunction<IN, Object> {

  @Override
  public Object apply(IN input) throws Exception {
    accept(input);
    return null;
  }

  public abstract void accept(IN input) throws Exception;

}
