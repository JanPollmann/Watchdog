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
  void runnable_ok() {
    ResultCounter<Object> resultCounter = new ResultCounter<>(result -> {
      Assertions.assertEquals(ResultCode.OK, result.getCode());
    });

    RepeatableTaskWithoutInput<Object> repeated = createRunnable(100, resultCounter.createDecoratedRunnable(() -> {
      Thread.sleep(50);
    }));

    assertRepeated(resultCounter, repeated, 20);
  }

  @Test
  @Timeout(4)
  void callable_ok() {
    ResultCounter<Integer> resultCounter = new ResultCounter<>(result -> {
      Assertions.assertEquals(ResultCode.OK, result.getCode());
      Assertions.assertEquals(42, result.getResult());
    });

    RepeatableTaskWithoutInput<Integer> repeated = createCallable(100, resultCounter.createDecoratedCallable(() -> {
      Thread.sleep(50);
      return 42;
    }));

    assertRepeated(resultCounter, repeated, 20);
  }

  private void assertRepeated(StoreResult<?> testSupport, RepeatableTaskWithoutInput<?> repeated, int loops) {
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

  private RepeatableTaskWithoutInput<Object> createRunnable(long timeoutInMilliseconds, Watchable<Object> runnable) {
    return withSingleThreadExecutor().createRepeated(timeoutInMilliseconds, runnable);
  }

  private RepeatableTaskWithoutInput<Integer> createCallable(long timeoutInMilliseconds, Watchable<Integer> callable) {
    return withSingleThreadExecutor().createRepeated(timeoutInMilliseconds, callable);
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
