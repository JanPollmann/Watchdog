package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.exceptions.WatchableInIncorrectState;
import de.pollmann.watchdog.exceptions.WatchableNotRepeatableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WatchableTest {

  @Test
  void buildRunnableWithInput_throwsException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Watchable.builder(() -> {}).withInput(new Object()));
  }

  @Test
  void buildCallableWithInput_throwsException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Watchable.builder(() -> null).withInput(new Object()));
  }

  @Test
  void watchableNotStarted_throwsException() {
    Watchable<?> watchable = Watchable.builder(() -> {}).build();

    Assertions.assertThrows(WatchableInIncorrectState.class, watchable::call);

    Assertions.assertDoesNotThrow(watchable::start);
  }

  @Test
  void watchableStarted_call_doesNotThrow_repeatedCallThrows() {
    Watchable<?> watchable = Watchable.builder(() -> {}).build();

    // start
    Assertions.assertDoesNotThrow(watchable::start);
    Assertions.assertThrows(WatchableNotRepeatableException.class, watchable::start);
    // call
    Assertions.assertDoesNotThrow(watchable::call);
    // repeated call
    Assertions.assertThrows(WatchableInIncorrectState.class, watchable::call);
  }

  @Test
  @Timeout(2)
  void watchableStop_canBeInterruptedToFreeWatchdogThreads() throws InterruptedException {
    WatchdogFactory watchdogFactory = new WatchdogFactory();
    Watchable<?> endless = Watchable.builder(() -> {
      int i = 1;
      while (i > 0) {
        i++;
        if (i == 100) {
          i = 1;
        }
      }
    }).withResultConsumer(result -> Assertions.fail()).build();
    Watchable<?> submittedCall = Watchable.builder(() -> {
      // this task cannot complete bcause of the internal task cannot complete!
      watchdogFactory.waitForCompletion(10, endless);
      Assertions.fail();
    }).build();

    Future<?> future = watchdogFactory.submitFunctionCall(100, submittedCall);
    try {
      future.get(1000, TimeUnit.MILLISECONDS);
    } catch (ExecutionException e) {
      Assertions.fail();
    } catch (TimeoutException e) {
      Assertions.assertFalse(submittedCall.stopped());
      Assertions.assertFalse(endless.stopped());
      future.cancel(true);
    }

    Thread.sleep(100);
    Assertions.assertTrue(submittedCall.stopped());
    Assertions.assertFalse(endless.stopped());

  }

}
