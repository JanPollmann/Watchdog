package de.pollmann.watchdog.tasks;

import java.util.Objects;
import java.util.concurrent.Callable;

class WatchableCallable<OUT> extends WatchableWithResultConsumer<OUT> {

  private final Callable<OUT> callable;

  private WatchableCallable(WatchableCallableBuilder<OUT> builder) {
    super(builder);
    callable = Objects.requireNonNull(builder.task);
  }

  @Override
  public OUT call() throws Exception {
    return callable.call();
  }

  static <OUT> WatchableCallableBuilder<OUT> builder(Callable<OUT> task) {
    return new WatchableCallableBuilder<>(task);
  }

  static final class WatchableCallableBuilder<OUT> extends WatchableBuilder<Object, OUT, Callable<OUT>, Watchable<OUT>> {

    private WatchableCallableBuilder(Callable<OUT> task) {
      super(task);
    }

    @Override
    public WatchableCallableBuilder<OUT> withInput(Object input) {
      throw new IllegalArgumentException("WatchableCallableBuilder does not support input.");
    }

    @Override
    public Watchable<OUT> build() {
      return new WatchableCallable<>(this);
    }

  }

}
