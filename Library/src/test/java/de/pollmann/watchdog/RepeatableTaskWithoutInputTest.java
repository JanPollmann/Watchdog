package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.testsupport.ResultCounter;
import de.pollmann.watchdog.testsupport.StoreResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class RepeatableTaskWithoutInputTest {

  @Test
  @Timeout(4)
  void runnable_ok() throws InterruptedException {
    ResultCounter<Object> resultCounter = new ResultCounter<>(result -> {
      Assertions.assertEquals(ResultCode.OK, result.getCode());
    });

    RepeatableTaskWithoutInput<Object> repeated = createRunnable(100, false, resultCounter.createDecoratedRunnable(() -> {
      Thread.sleep(50);
    }));

    assertRepeated(resultCounter, repeated, 20);
    // 0 because statistics are disabled
    Assertions.assertEquals(0, repeated.getCallsPerSecond());
  }

  @Test
  @Timeout(2)
  void runnable_singleCall_ok() throws InterruptedException {
    ResultCounter<Object> resultCounter = new ResultCounter<>(result -> {
      Assertions.assertEquals(ResultCode.OK, result.getCode());
    });

    RepeatableTaskWithoutInput<Object> repeated = createRunnable(100, true, resultCounter.createDecoratedRunnable(() -> {

    }));

    assertRepeated(resultCounter, repeated, 1);

    try {
      // sleep to finish the timeframe of 1000 ms
      Thread.sleep(1100);
    } catch (InterruptedException e) {
      Assertions.fail(e);
    }

    // because:
    // statistics array fills over 10 seconds, one entry per second
    // two runnable's are submitted
    // 2 loops / 10 seconds = 0.2
    Assertions.assertEquals(0.2, repeated.getCallsPerSecond());
  }

  @Test
  @Timeout(4)
  void callable_ok() throws InterruptedException {
    ResultCounter<Integer> resultCounter = new ResultCounter<>(result -> {
      Assertions.assertEquals(ResultCode.OK, result.getCode());
      Assertions.assertEquals(42, result.getResult());
    });

    RepeatableTaskWithoutInput<Integer> repeated = createCallable(100, false, resultCounter.createDecoratedCallable(() -> {
      Thread.sleep(50);
      return 42;
    }));

    assertRepeated(resultCounter, repeated, 20);
    // 0 because statistics are disabled
    Assertions.assertEquals(0, repeated.getCallsPerSecond());
  }

  private void assertRepeated(StoreResult<?> testSupport, RepeatableTaskWithoutInput<?> repeated, int loops) throws InterruptedException {
    for (int i = 0; i < loops; i++) {
      TaskResult<?> result = repeated.waitForCompletion();
      Assertions.assertEquals(ResultCode.OK, result.getCode());
    }
    Assertions.assertEquals(loops, testSupport.getFinishedCounter());

    List<Future<?>> futureList = new ArrayList<>();
    for(int i = 0; i < loops; i++) {
      futureList.add(repeated.submitFunctionCall());
    }
    while (!futuresFinished(futureList)) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Assertions.fail(e);
      }
    }
    Assertions.assertEquals(2*loops, testSupport.getFinishedCounter());
  }

  private boolean futuresFinished(List<Future<?>> futures) {
    for(Future<?> future : futures) {
      if (!future.isDone()) {
        return false;
      }
    }
    return true;
  }

  private RepeatableTaskWithoutInput<Object> createRunnable(long timeoutInMilliseconds, boolean monitored, Watchable<Object> runnable) {
    if (monitored) {
      return withSingleThreadExecutor().createRepeated(timeoutInMilliseconds, true, runnable);
    } else {
      return withSingleThreadExecutor().createRepeated(timeoutInMilliseconds, runnable);
    }
  }

  private RepeatableTaskWithoutInput<Integer> createCallable(long timeoutInMilliseconds, boolean monitored, Watchable<Integer> callable) {
    if (monitored) {
      return withSingleThreadExecutor().createRepeated(timeoutInMilliseconds, true, callable);
    } else {
      return withSingleThreadExecutor().createRepeated(timeoutInMilliseconds, callable);
    }
  }

  private static WatchdogFactory withSingleThreadExecutor() {
    return new WatchdogFactory(
      newSingleThreadExecutor("test:watchdog"),
      newSingleThreadExecutor("test:worker")
    );
  }

  private static ExecutorService newSingleThreadExecutor(String threadPrefix) {
    return Executors.newSingleThreadExecutor(WatchdogFactory.createDefaultThreadFactory(threadPrefix));
  }

}
