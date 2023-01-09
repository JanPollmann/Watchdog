package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.WatchableFunction;

import java.util.function.Function;

public class WrappedWatchableFunction<IN, OUT> extends WatchableFunction<IN, OUT> {

  private final Function<IN,OUT> function;

  public WrappedWatchableFunction(Function<IN,OUT> function, IN data) {
    super(data);
    this.function = function;
  }

  @Override
  public OUT apply(IN input) throws Exception {
    return function.apply(input);
  }

  @Override
  public WrappedWatchableFunction<IN, OUT> clone(IN newInput) {
    return new WrappedWatchableFunction<>(function, newInput);
  }

}
