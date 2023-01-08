package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.WatchableCallable;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class WatchableCallableForTest extends WatchableForTest<Integer> implements WatchableCallable<Integer> {

  private final Callable<Integer> callable;

  public WatchableCallableForTest(Callable<Integer> callable) {
    this.callable = callable;
  }

  @Override
  public Integer call() throws Exception {
    return callable.call();
  }

  public static WatchableCallableForTest submitWatchable(WatchdogFactory factory, long timeoutInMs, Callable<Integer> callable) {
    WatchableCallableForTest watchableCallableForTest = new WatchableCallableForTest(callable);
    Future<?> watched = factory.submitFunctionCall(timeoutInMs, watchableCallableForTest);
    while (!watched.isDone()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Assertions.fail(e);
      }
    }
    return watchableCallableForTest;
  }
}