package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.ExceptionConsumer;
import de.pollmann.watchdog.tasks.ExceptionRunnable;
import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.tasks.WatchableWithInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SabotageTest {

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
   * The runnable {@link Sabotage} will block the ExecutorService!
   */
  @Test
  @Timeout(2)
  void runnable_userTriesEverythingToSabotageTheTimeout_TIMEOUT_threadIsNeverFinished() throws InterruptedException {
    WatchdogFactory watchdogFactory = new WatchdogFactory(2);
    Watchable<Object> sabotage =  Watchable.builder(new Sabotage()).build();
    AtomicBoolean sabotageStarted = new AtomicBoolean(false);
    AtomicBoolean sabotageStopped = new AtomicBoolean(false);
    AtomicBoolean wasInterrupted = new AtomicBoolean(false);
    // wrapp the call 'sabotage' in a timeout
    //    => interrupt
    //    => cancel the wrapped call but leaves the Thread of the Executor Service in an infinite loop
    //    => at some point the watchdogFactory does not have any thread remaining
    TaskResult<?> wrappedCall = watchdogFactory.waitForCompletion(WatchableOptions.builder(1300).build(), Watchable.builder(() -> {
      sabotageStarted.set(true);
      try {
        // timeout can NOT kill the Thread because the submitted task is not interruptable
        TaskResult<?> neverCreated = watchdogFactory.waitForCompletion(WatchableOptions.builder(1000).build(), sabotage);
        // the runnable does not respond to an interrupt => not stopped => infinite loop
        // unreachable ...
        sabotageStopped.set(true);
      } catch (InterruptedException interruptedException) {
        // But "waitForCompletion" itself is interruptable
        wasInterrupted.set(true);
        throw interruptedException;
      }
    }).build());

    Assertions.assertTrue(wasInterrupted.get());
    Assertions.assertFalse(sabotageStopped.get());
    Assertions.assertTrue(sabotageStarted.get());
    Assertions.assertFalse(sabotage.stopped());
    assertTimeout(wrappedCall);
  }

  /**
   * This runnable will not the ExecutorService because of the {@link InterruptedException}
   */
  private static class NiceSabotageRunnable implements ExceptionRunnable {

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

  /**
   * The runnable {@link NiceSabotageRunnable} will NOT block the ExecutorService!
   */
  @Test
  @Timeout(10)
  void runnable_endlessLoopRespondingToInterrupts_TIMEOUT_threadGetsInterrupted() throws InterruptedException {
    WatchdogFactory watchdogFactory = new WatchdogFactory();

    for (int i = 0; i < 100; i++) {
      Watchable<Object> niceSabotage =  Watchable.builder(new NiceSabotageRunnable()).build();
      assertTimeout(watchdogFactory.waitForCompletion(WatchableOptions.builder(50).build(), niceSabotage));
      // the runnable does respond to an interrupt => stopped => no endless loop
      Assertions.assertTrue(niceSabotage.stopped());
    }

  }

  @Test
  @Timeout(10)
  void consumer_endlessLoopRespondingToInterrupts_TIMEOUT_threadGetsInterrupted() throws InterruptedException {
    WatchdogFactory watchdogFactory = new WatchdogFactory();

    for (int i = 0; i < 100; i++) {
      Watchable<Object> niceSabotage =  Watchable.builder(new NiceSabotageConsumer()).build();
      assertTimeout(watchdogFactory.waitForCompletion(WatchableOptions.builder(50).build(), niceSabotage));
      // the runnable does respond to interrupts => stopped => no endless loop
      Assertions.assertTrue(niceSabotage.stopped());
    }

  }

  @Test
  @Timeout(10)
  void repeatableRunnable_endlessLoopRespondingToInterrupts_TIMEOUT_threadGetsInterrupted() throws InterruptedException {
    WatchdogFactory watchdogFactory = new WatchdogFactory();
    Watchable<Object> niceSabotage =  Watchable.builder(new NiceSabotageRunnable())
      .withResultConsumer(result -> Assertions.assertTrue(result.getWatchable().stopped()))
      .build();
    RepeatableTaskWithoutInput<Object> repeatable = watchdogFactory.createRepeated(WatchableOptions.builder(50).build(), niceSabotage);

    for (int i = 0; i < 100; i++) {
      assertTimeout(repeatable.waitForCompletion());
    }

  }

  @Test
  @Timeout(10)
  void repeatableConsumer_endlessLoopRespondingToInterrupts_TIMEOUT_threadGetsInterrupted() throws InterruptedException {
    WatchdogFactory watchdogFactory = new WatchdogFactory();
    WatchableWithInput<Integer, Object> niceSabotage =  Watchable.builder(new NiceSabotageConsumer())
      .withResultConsumer(result -> Assertions.assertTrue(result.getWatchable().stopped()))
      .build();
    RepeatableTaskWithInput<Integer, Object> repeatable = watchdogFactory.createRepeated(WatchableOptions.builder(50).build(), niceSabotage);

    for (int i = 0; i < 100; i++) {
      assertTimeout(repeatable.waitForCompletion(i));
    }

  }

  private void assertTimeout(TaskResult<?> result) {
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.hasError());
    Assertions.assertEquals(ResultCode.TIMEOUT, result.getCode(), String.format("Error: %s", result.getErrorReason()));
    Assertions.assertNotNull(result.getErrorReason());
    Assertions.assertNull(result.getResult());
    Assertions.assertTrue(result.getErrorReason() instanceof TimeoutException);
  }

  /**
   * This runnable will not the ExecutorService because of the {@link InterruptedException}
   */
  private static class NiceSabotageConsumer implements ExceptionConsumer<Integer> {

    @Override
    public void accept(Integer t) throws Exception {
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
