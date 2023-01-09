package de.pollmann.watchdog;

public class RepeatableTaskTerminatedException extends RuntimeException {
  public RepeatableTaskTerminatedException(RepeatableTask repeatableTask) {
    super(String.format("Cannot invoke repeatable task [%s]: The repeatable task has been terminated.", repeatableTask));
  }
}
