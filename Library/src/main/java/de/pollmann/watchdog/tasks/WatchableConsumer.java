package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

import java.util.function.Consumer;
import java.util.function.Function;

public class WatchableConsumer<IN> extends WatchableFunction<IN, Object> {

  public WatchableConsumer(Consumer<TaskResult<Object>> resultConsumer, Function<IN, Object> function, IN input) {
    super(resultConsumer, function, input);
  }

  public WatchableConsumer(Function<IN, Object> function, IN input) {
    super(function, input);
  }

  public WatchableConsumer(Consumer<TaskResult<Object>> resultConsumer, Consumer<IN> consumer, IN input) {
    this(resultConsumer, in -> {
      consumer.accept(in); return null;
    }, input);
  }

  public WatchableConsumer(Consumer<IN> consumer, IN input) {
    this(in -> {
      consumer.accept(in); return null;
    }, input);
  }

  @Override
  public WatchableConsumer<IN> clone(IN newValue) {
    return new WatchableConsumer<>(resultConsumer, function, newValue);
  }

}
