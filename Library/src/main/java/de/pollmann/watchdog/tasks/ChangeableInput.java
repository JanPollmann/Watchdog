package de.pollmann.watchdog.tasks;

public interface ChangeableInput<IN, OUT> {
  WatchableBuilder<IN, OUT, ?, ? extends WatchableWithInput<IN, OUT>> newInput(IN newValue);
}
