package de.pollmann.watchdog.tasks;

@FunctionalInterface
public interface ExceptionFunction<IN, OUT> {
  OUT apply(IN input) throws Exception;
}
