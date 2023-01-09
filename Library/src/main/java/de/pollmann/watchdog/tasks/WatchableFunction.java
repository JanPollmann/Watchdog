package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class WatchableFunction<IN, OUT> implements Watchable<OUT> {

  protected final IN input;
  protected final Consumer<TaskResult<OUT>> resultConsumer;
  protected final Function<IN,OUT> function;

  public WatchableFunction(Consumer<TaskResult<OUT>> resultConsumer, Function<IN, OUT> function, IN input) {
    this.resultConsumer = Objects.requireNonNull(resultConsumer);
    this.function = function;
    this.input = input;
  }

  public WatchableFunction(Function<IN, OUT> function, IN input) {
    this.resultConsumer = WatchableFunction::emptyResultConsumer;
    this.function = function;
    this.input = input;
  }

  @Override
  public OUT call() throws Exception {
    return apply(input);
  }

  @Override
  public final void finishedWithResult(TaskResult<OUT> result) {
    resultConsumer.accept(result);
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
