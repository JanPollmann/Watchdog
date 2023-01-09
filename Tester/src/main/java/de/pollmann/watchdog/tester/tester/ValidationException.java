package de.pollmann.watchdog.tester.tester;

public class ValidationException extends Exception {
  public ValidationException(Tester tester, String message) {
    super(message);
  }
}
