package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.WatchableWithInput;
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
import java.util.concurrent.atomic.AtomicInteger;

class RepeatableTaskWithInputTest {

  @Test
  @Timeout(4)
  void consumer_ok() {
    AtomicInteger sum = new AtomicInteger();
    ResultCounter<Object> resultCounter = new ResultCounter<>(result -> {
      Assertions.assertEquals(ResultCode.OK, result.getCode());
    });

    RepeatableTaskWithInput<Integer, Object> repeated = createConsumer(100, resultCounter.createDecoratedConsumer(
      sum::addAndGet
    ));

    assertRepeated(resultCounter, repeated, 10);
    Assertions.assertEquals(90, sum.get());
  }

  @Test
  @Timeout(4)
  void function_ok() {
    AtomicInteger sum = new AtomicInteger();
    ResultCounter<Integer> resultCounter = new ResultCounter<>(result -> {
      Assertions.assertEquals(ResultCode.OK, result.getCode());
      Assertions.assertEquals(42, result.getResult());
    });

    RepeatableTaskWithInput<Integer, Integer> repeated = createFunction(100, resultCounter.createDecoratedFunction(input -> {
      sum.addAndGet(input);
      return 42;
    }));

    assertRepeated(resultCounter, repeated, 11);
    Assertions.assertEquals(110, sum.get());
  }

  private void assertRepeated(StoreResult<?> testSupport, RepeatableTaskWithInput<Integer, ?> repeated, int loops) {
    for (int i = 0; i < loops; i++) {
      TaskResult<?> result = repeated.waitForCompletion(i);
      Assertions.assertEquals(ResultCode.OK, result.getCode());
    }
    Assertions.assertEquals(loops, testSupport.getFinishedCounter());

    List<Future<?>> futureList = new ArrayList<>();
    for(int i = 0; i < loops; i++) {
      futureList.add(repeated.submitFunctionCall(i));
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

  private RepeatableTaskWithInput<Integer, Object> createConsumer(long timeoutInMilliseconds, WatchableWithInput<Integer, Object> consumer) {
    return withSingleThreadExecutor().createRepeated(timeoutInMilliseconds, consumer);
  }

  private RepeatableTaskWithInput<Integer, Integer> createFunction(long timeoutInMilliseconds, WatchableWithInput<Integer, Integer> function) {
    return withSingleThreadExecutor().createRepeated(timeoutInMilliseconds, function);
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
