package de.pollmann.watchdog.tasks;

@FunctionalInterface
public interface WatchableRunnable extends Watchable<Object> {
  void run() throws Exception;

  @Override
  default Object call() throws Exception {
    run();
    return null;
  }
}
