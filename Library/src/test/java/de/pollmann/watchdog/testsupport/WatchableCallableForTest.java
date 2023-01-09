package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.WatchableCallable;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class WatchableCallableForTest implements WatchableCallable<Integer>, StoreResult<Integer> {

  private final Callable<Integer> callable;

  private TaskResult<Integer> lastResult = null;
  private int finishedCounter = 0;

  public WatchableCallableForTest(Callable<Integer> callable) {
    this.callable = callable;
  }

  @Override
  public Integer call() throws Exception {
    return callable.call();
  }

  @Override
  public TaskResult<Integer> getLastResult() {
    return lastResult;
  }

  @Override
  public int getFinishedCounter() {
    return finishedCounter;
  }

  @Override
  public void finishedWithResult(TaskResult<Integer> result) {
    WatchableCallable.super.finishedWithResult(result);
    lastResult = result;
    finishedCounter++;
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