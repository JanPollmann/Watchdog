package de.pollmann.watchdog.tasks;

import java.util.Objects;

class WatchableRunnable extends WatchableWithResultConsumer<Object> {

  private final ExceptionRunnable runnable;

  private WatchableRunnable(WatchableRunnableBuilder builder) {
    super(builder);
    runnable = Objects.requireNonNull(builder.task);
  }

  @Override
  public Object call() throws Exception {
    runnable.run();
    return null;
  }

  static WatchableRunnableBuilder builder(ExceptionRunnable task) {
    return new WatchableRunnableBuilder(task);
  }

  static class WatchableRunnableBuilder extends WatchableBuilder<Object, Object, ExceptionRunnable, Watchable<Object>> {

    private WatchableRunnableBuilder(ExceptionRunnable task) {
      super(task);
    }

    @Override
    public WatchableRunnableBuilder withInput(Object input) {
      throw new IllegalArgumentException("WatchableRunnableBuilder does not support input.");
    }

    @Override
    public Watchable<Object> build() {
      return new WatchableRunnable(this);
    }

  }

}
