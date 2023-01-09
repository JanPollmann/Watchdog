package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.WatchableConsumer;

import java.util.function.Consumer;

public class WrappedWatchableConsumer<IN> extends WatchableConsumer<IN> {

  private final Consumer<IN> consumer;

  public WrappedWatchableConsumer(Consumer<IN> consumer, IN data) {
    super(data);
    this.consumer = consumer;
  }

  @Override
  public void accept(IN input) throws Exception {
    consumer.accept(input);
  }

  @Override
  public WrappedWatchableConsumer<IN> clone(IN newInput) {
    return new WrappedWatchableConsumer<>(consumer, newInput);
  }
}
