package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.WatchableRunnable;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.Future;

public class WatchableRunnableForTest implements WatchableRunnable, StoreResult<Object> {

  private final WatchableRunnable runnable;

  private TaskResult<Object> lastResult = null;
  private int finishedCounter = 0;

  public WatchableRunnableForTest(WatchableRunnable runnable) {
    this.runnable = runnable;
  }

  @Override
  public void run() throws Exception {
    runnable.run();
  }

  @Override
  public TaskResult<Object> getLastResult() {
    return lastResult;
  }

  @Override
  public int getFinishedCounter() {
    return finishedCounter;
  }

  @Override
  public void finishedWithResult(TaskResult<Object> result) {
    WatchableRunnable.super.finishedWithResult(result);
    lastResult = result;
    finishedCounter++;
  }

  public static WatchableRunnableForTest submitWatchable(WatchdogFactory factory, long timeoutInMs, WatchableRunnable runnable) {
    WatchableRunnableForTest watchableRunnableForTest = new WatchableRunnableForTest(runnable);
    Future<?> watched = factory.submitFunctionCall(timeoutInMs, watchableRunnableForTest);
    while (!watched.isDone()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Assertions.fail(e);
      }
    }
    return watchableRunnableForTest;
  }

}
