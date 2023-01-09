package de.pollmann.watchdog;

import de.pollmann.watchdog.testsupport.*;
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
    WatchableConsumerForTest watchableConsumerForTest = new WatchableConsumerForTest(result -> {
      Assertions.assertEquals(ResultCode.OK, result.getCode());
    }, sum::addAndGet, 0);
    RepeatableTaskWithInput<Integer, Object> repeated = create(100, watchableConsumerForTest);

    assertRepeated(watchableConsumerForTest, repeated, 10);
    Assertions.assertEquals(90, sum.get());
  }

  @Test
  @Timeout(4)
  void function_ok() {
    AtomicInteger sum = new AtomicInteger();
    WatchableFunctionForTest watchableFunctionForTest = new WatchableFunctionForTest(result -> {
      Assertions.assertEquals(ResultCode.OK, result.getCode());
      Assertions.assertEquals(42, result.getResult());
    }, input -> {
      sum.addAndGet(input);
      return 42;
    }, 0);
    RepeatableTaskWithInput<Integer, Integer> repeated = create(100, watchableFunctionForTest);

    assertRepeated(watchableFunctionForTest, repeated, 11);
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

  private RepeatableTaskWithInput<Integer, Object> create(long timeoutInMilliseconds, WatchableConsumerForTest consumer) {
    return withSingleThreadExecutor().createRepeated(timeoutInMilliseconds, consumer);
  }

  private RepeatableTaskWithInput<Integer, Integer> create(long timeoutInMilliseconds, WatchableFunctionForTest function) {
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
