package de.pollmann.watchdog.tasks;

interface ChangeableInput<IN, OUT> {
  Watchable<OUT> newInput(IN newValue);
}
