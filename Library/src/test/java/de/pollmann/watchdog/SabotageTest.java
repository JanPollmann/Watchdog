package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.ExceptionRunnable;
import de.pollmann.watchdog.tasks.Watchable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SabotageTest {

  /**
   * The runnable will block the ExecutorService!
   */
  @Test
  @Timeout(2)
  void runnable_userTriesEverythingToSabotageTheTimeout_TIMEOUT_threadIsNeverFinished() throws InterruptedException {
    WatchdogFactory watchdogFactory = new WatchdogFactory(2);
    Watchable<Object> sabotage =  Watchable.builder(new Sabotage()).build();
    AtomicBoolean sabotageStarted = new AtomicBoolean(false);
    AtomicBoolean sabotageStopped = new AtomicBoolean(false);
    TaskResult<?> taskResult = watchdogFactory.waitForCompletion(1300, Watchable.builder(() -> {
      sabotageStarted.set(true);
      watchdogFactory.waitForCompletion(1000, sabotage);
      // the runnable does not respond to interrupts => no stopped => endless loop
      // unreachable ...
      sabotageStopped.set(true);
    }).build());

    Assertions.assertFalse(sabotageStopped.get());
    Assertions.assertTrue(sabotageStarted.get());
    Assertions.assertFalse(sabotage.stopped());
    assertTimeout(taskResult);
  }

  @Test
  @Timeout(10)
  void runnable_endlessLoopRespondingToInterrupts_TIMEOUT_threadGetsInterrupted() throws InterruptedException {
    WatchdogFactory watchdogFactory = new WatchdogFactory();

    for (int i = 0; i < 100; i++) {
      Watchable<Object> niceSabotage =  Watchable.builder(new NiceSabotage()).build();
      assertTimeout(watchdogFactory.waitForCompletion(50, niceSabotage));
      // the runnable does respond to interrupts => stopped => no endless loop
      Assertions.assertTrue(niceSabotage.stopped());
    }

  }

  private void assertTimeout(TaskResult<?> result) {
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.hasError());
    Assertions.assertEquals(ResultCode.TIMEOUT, result.getCode());
    Assertions.assertNotNull(result.getErrorReason());
    Assertions.assertNull(result.getResult());
    Assertions.assertTrue(result.getErrorReason() instanceof TimeoutException);
  }

  /**
   * This runnable will block the ExecutorService, not even a timeout occur (thread cannot be interrupted!)
   */
  private static class Sabotage implements ExceptionRunnable {
    @Override
    public void run() throws Exception {
      int i = 1;
      while (i > 0) {
        i++;
        if (i >= 1000) {
          i = 1;
        }
      }
    }
  }

  /**
   * This runnable will not the ExecutorService because of the {@link InterruptedException}
   */
  private static class NiceSabotage implements ExceptionRunnable {

    @Override
    public void run() throws Exception {
      int i = 1;
      while (i > 0) {
        // add this if to your code to stop the runnable if a timeout occurred
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        // e.g. thread sleep does that as well
        Thread.sleep(10);
        i++;
        if (i >= 1000) {
          i = 1;
        }
      }
    }

  }

}
