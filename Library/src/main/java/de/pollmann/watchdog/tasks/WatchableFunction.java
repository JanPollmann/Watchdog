package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

import java.util.function.Consumer;
import java.util.function.Function;

public class WatchableFunction<IN, OUT> implements Watchable<OUT> {

  protected final IN input;
  protected final Function<IN,OUT> function;

  private Consumer<TaskResult<OUT>> resultConsumer;

  public WatchableFunction(Consumer<TaskResult<OUT>> resultConsumer, Function<IN, OUT> function, IN input) {
    this.function = function;
    this.input = input;
    setResultConsumer(resultConsumer);
  }

  public WatchableFunction(Function<IN, OUT> function, IN input) {
    this(null, function, input);
  }

  @Override
  public OUT call() throws Exception {
    return apply(input);
  }

  public void setResultConsumer(Consumer<TaskResult<OUT>> resultConsumer) {
    this.resultConsumer = resultConsumer;
  }

  @Override
  public Consumer<TaskResult<OUT>> getResultConsumer() {
    return resultConsumer;
  }

  public OUT apply(IN input) throws Exception {
    return function.apply(input);
  }

  public WatchableFunction<IN, OUT> clone(IN newInput) {
    return new WatchableFunction<>(resultConsumer, function, newInput);
  }

  protected static void emptyResultConsumer(TaskResult<?> result) {
    // no op
  }
}
