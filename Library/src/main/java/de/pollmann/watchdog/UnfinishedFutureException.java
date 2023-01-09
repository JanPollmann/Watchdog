package de.pollmann.watchdog;

import java.util.concurrent.Future;

public class UnfinishedFutureException extends Exception {

  private final Future<?> future;

  public UnfinishedFutureException(Future<?> future) {
    super("A future was not finished.");
    this.future = future;
  }

  public Future<?> getFuture() {
    return future;
  }

}
