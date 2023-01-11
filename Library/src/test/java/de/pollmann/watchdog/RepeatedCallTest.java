package de.pollmann.watchdog;

import de.pollmann.watchdog.exceptions.WatchableNotRepeatableException;
import de.pollmann.watchdog.tasks.Watchable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class RepeatedCallTest {

  private static final int REPEATED = 100;

  private final WatchdogFactory watchdogFactory = new WatchdogFactory(8);

  @Test
  @Timeout(10)
  void singleWatchableSubmitted_oneCompletes() {
    final List<TaskResult<Integer>> results = new ArrayList<>();
    CountDownLatch countDown = new CountDownLatch(REPEATED);
    AtomicInteger called = new AtomicInteger(0);
    final Watchable<Integer> watchable = Watchable.builder(() -> {
      Thread.sleep(10);
      return called.incrementAndGet();
    }).withResultConsumer(result -> {
      synchronized (results) {
        results.add(result);
      }
      countDown.countDown();
    }).build();

    for (int i = 0; i < REPEATED; i++) {
      watchdogFactory.submitFunctionCall(100, watchable);
    }
    try {
      countDown.await();
    } catch (InterruptedException e) {
      Assertions.fail();
    }

    Assertions.assertEquals(REPEATED, results.size());
    Assertions.assertEquals(1, called.get());

    int numberOfResultOk = 0;
    for (TaskResult<Integer> result : results) {
      Assertions.assertTrue(result.getWatchable().stopped());
      if (result.getCode() == ResultCode.OK) {
        numberOfResultOk++;
      } else {
        Assertions.assertEquals(ResultCode.ERROR, result.getCode(), String.format("ErrorCode: %s", result.getCode()));
        Assertions.assertTrue(result.hasError());
        Assertions.assertTrue(result.getErrorReason() instanceof WatchableNotRepeatableException, String.format("ErrorReason: %s", result.getErrorReason()));
      }
    }
    Assertions.assertEquals(1, numberOfResultOk);

  }

}
