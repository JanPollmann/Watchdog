package de.pollmann.watchdog.tasks;

@FunctionalInterface
public interface ExceptionRunnable {
  void run() throws Exception;
}
