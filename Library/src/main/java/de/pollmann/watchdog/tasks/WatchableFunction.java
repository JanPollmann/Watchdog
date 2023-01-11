package de.pollmann.watchdog.tasks;

import java.util.Objects;

class WatchableFunction<IN, OUT> extends WatchableWithResultConsumer<OUT> implements WatchableWithInput<IN, OUT> {

  private final ExceptionFunction<IN, OUT> function;
  private final IN input;

  private WatchableFunction(WatchableFunctionBuilder<IN, OUT> builder) {
    super(builder);
    this.function = Objects.requireNonNull(builder.task);
    this.input = builder.input;
  }

  @Override
  protected OUT wrappedCall() throws Exception {
    return function.apply(input);
  }

  static <IN, OUT> WatchableFunctionBuilder<IN, OUT> builder(ExceptionFunction<IN, OUT> task) {
    return new WatchableFunctionBuilder<>(task);
  }

  @Override
  public WatchableBuilder<IN, OUT, ?, ? extends WatchableWithInput<IN, OUT>> newInput(IN newValue) {
    return copy().withInput(newValue);
  }

  @Override
  public WatchableBuilder<IN, OUT, ?, ? extends WatchableWithInput<IN, OUT>> copy() {
    return builder(function)
      .withResultConsumer(resultConsumer);
  }

  static class WatchableFunctionBuilder<IN, OUT> extends WatchableBuilder<IN, OUT, ExceptionFunction<IN, OUT>, WatchableWithInput<IN, OUT>> {

    protected WatchableFunctionBuilder(ExceptionFunction<IN, OUT> task) {
      super(task);
    }

    @Override
    public WatchableWithInput<IN, OUT> build() {
      return new WatchableFunction<>(this);
    }

  }

}
