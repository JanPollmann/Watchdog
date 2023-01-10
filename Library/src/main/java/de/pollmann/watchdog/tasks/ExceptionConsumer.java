package de.pollmann.watchdog.tasks;

@FunctionalInterface
public interface ExceptionConsumer<IN> {
  void accept(IN t) throws Exception;
}
