package de.pollmann.watchdog.testsupport;

import java.util.function.Consumer;

class ConsumerProxy<T> implements Consumer<T> {

  private Consumer<T> consumer = null;

  public ConsumerProxy() {

  }

  public ConsumerProxy(Consumer<T> consumer) {
    setConsumer(consumer);
  }

  public void setConsumer(Consumer<T> consumer) {
    this.consumer = consumer;
  }

  public Consumer<T> getConsumer() {
    return consumer;
  }

  @Override
  public void accept(T t) {
    if (consumer != null) {
      consumer.accept(t);
    }
  }
}
