package de.pollmann.watchdog.tasks;

import java.util.function.Function;

public abstract class WatchableFunction<IN, OUT> implements Watchable<OUT>, Function<IN, OUT> {

  private IN data = null;

  public void setData(IN data) {
    this.data = data;
  }

  public IN getData() {
    return data;
  }

  @Override
  public OUT call() throws Exception {
    return apply(getData());
  }
}
