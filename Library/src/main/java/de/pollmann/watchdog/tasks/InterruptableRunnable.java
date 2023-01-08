package de.pollmann.watchdog.tasks;

@FunctionalInterface
public interface InterruptableRunnable {
  void run() throws InterruptedException;
}
