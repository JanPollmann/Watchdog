package de.pollmann.watchdog;

import de.pollmann.watchdog.testsupport.StoreResult;
import de.pollmann.watchdog.testsupport.WatchableCallableForTest;
import de.pollmann.watchdog.testsupport.WatchableRunnableForTest;
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
    WatchableRunnableForTest watchableRunnableForTest = new WatchableRunnableForTest(() -> {
      Thread.sleep(50);
    }) {
      @Override
      public void finishedWithResult(TaskResult<Object> result) {
        super.finishedWithResult(result);
        Assertions.assertEquals(ResultCode.OK, result.getCode());
      }
    };
    RepeatableTaskWithoutInput<Object> repeated = create(100, watchableRunnableForTest);

    assertRepeated(watchableRunnableForTest, repeated, 20);
  }

  @Test
  @Timeout(4)
  void callable_ok() {
    WatchableCallableForTest watchableCallableForTest = new WatchableCallableForTest(() -> {
      Thread.sleep(50);
      return 42;
    }) {
      @Override
      public void finishedWithResult(TaskResult<Integer> result) {
        super.finishedWithResult(result);
        Assertions.assertEquals(ResultCode.OK, result.getCode());
        Assertions.assertEquals(42, result.getResult());
      }
    };
    RepeatableTaskWithoutInput<Integer> repeated = create(100, watchableCallableForTest);

    assertRepeated(watchableCallableForTest, repeated, 20);
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

  private RepeatableTaskWithoutInput<Object> create(long timeoutInMilliseconds, WatchableRunnableForTest runnable) {
    return withSingleThreadExecutor().createRepeated(timeoutInMilliseconds, runnable);
  }

  private RepeatableTaskWithoutInput<Integer> create(long timeoutInMilliseconds, WatchableCallableForTest callable) {
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
