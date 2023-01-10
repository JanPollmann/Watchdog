package de.pollmann.watchdog.tasks;

import java.util.Objects;

class WatchableConsumer<IN> extends WatchableWithResultConsumer<Object> implements WatchableWithInput<IN, Object> {

  private final ExceptionConsumer<IN> consumer;
  private final IN input;

  private WatchableConsumer(WatchableConsumerBuilder<IN> builder) {
    super(builder);
    consumer = Objects.requireNonNull(builder.task);
    input = builder.input;
  }

  @Override
  public Object call() throws Exception {
    consumer.accept(input);
    return null;
  }

  @Override
  public Watchable<Object> newInput(IN newValue) {
    return new WatchableConsumerBuilder<>(consumer)
      .withResultConsumer(resultConsumer)
      .withInput(newValue)
      .build();
  }

  static <IN> WatchableConsumerBuilder<IN> builder(ExceptionConsumer<IN> task) {
    return new WatchableConsumerBuilder<>(task);
  }

  static class WatchableConsumerBuilder<IN> extends WatchableBuilder<IN, Object, ExceptionConsumer<IN>, WatchableWithInput<IN, Object>> {

    protected WatchableConsumerBuilder(ExceptionConsumer<IN> task) {
      super(task);
    }

    @Override
    public WatchableWithInput<IN, Object> build() {
      return new WatchableConsumer<>(this);
    }

  }

}
