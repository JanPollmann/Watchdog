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
  protected Object wrappedCall() throws Exception {
    consumer.accept(input);
    return null;
  }

  static <IN> WatchableConsumerBuilder<IN> builder(ExceptionConsumer<IN> task) {
    return new WatchableConsumerBuilder<>(task);
  }

  @Override
  public WatchableBuilder<IN, Object, ?, ? extends WatchableWithInput<IN, Object>> newInput(IN newValue) {
    return copy().withInput(newValue);
  }

  @Override
  public WatchableBuilder<IN, Object, ?, ? extends WatchableWithInput<IN, Object>> copy() {
    return builder(consumer)
      .withResultConsumer(resultConsumer);
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
