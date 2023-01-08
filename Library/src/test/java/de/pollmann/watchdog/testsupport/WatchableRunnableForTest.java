package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.InterruptableRunnable;
import de.pollmann.watchdog.tasks.WatchableRunnable;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.Future;

public class WatchableRunnableForTest extends WatchableForTest<Object> implements WatchableRunnable {

  private final InterruptableRunnable runnable;

  public WatchableRunnableForTest(InterruptableRunnable runnable) {
    this.runnable = runnable;
  }

  @Override
  public void run() throws InterruptedException {
    runnable.run();
  }

  public static WatchableRunnableForTest submitWatchable(WatchdogFactory factory, long timeoutInMs, InterruptableRunnable runnable) {
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
