package de.pollmann.watchdog.testsupport;

import java.util.Objects;
import java.util.function.Consumer;

public class ComposedConsumer<T> implements Consumer<T> {

  private final Consumer<T> first;
  private final Consumer<T> second;

  public ComposedConsumer(Consumer<T> first, Consumer<T> second) {
    this.first = Objects.requireNonNull(first);
    this.second = Objects.requireNonNull(second);
  }

  @Override
  public void accept(T t) {
    first.accept(t);
    second.accept(t);
  }

}
