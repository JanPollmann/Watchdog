package de.pollmann.watchdog.tasks;

public abstract class WatchableConsumer<IN> extends WatchableFunction<IN, Object> {

  public WatchableConsumer(IN input) {
    super(input);
  }

  @Override
  public Object apply(IN input) throws Exception {
    accept(input);
    return null;
  }

  public abstract void accept(IN input) throws Exception;

  @Override
  public abstract WatchableConsumer<IN> clone(IN newValue);

}
