package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.WatchableRunnable;
import de.pollmann.watchdog.testsupport.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

class WatchdogFactoryTest {

  private final WatchdogFactory watchdogFactory = new WatchdogFactory();

  @Test
  @Timeout(2)
  void runnable_OK() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {});

    assertRunnableResultWithOk(result);
  }

  @Test
  @Timeout(2)
  void runnable_OK_submit() throws InterruptedException {
    WatchableRunnableForTest runnable = WatchableRunnableForTest.submitWatchable(watchdogFactory, 1000, () -> {});

    assertRunnableResultWithOk(runnable.getLastResult());
  }

  @Test
  @Timeout(2)
  void callable_out50_OK() {
    int returned = 50;
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {
      Thread.sleep(500);
      return returned;
    });

    assertCallableWithOkAndResult(result, returned);
  }

  @Test
  @Timeout(2)
  void callable_out50_OK_submit() {
    int returned = 50;
    WatchableCallableForTest callable = WatchableCallableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      Thread.sleep(500);
      return returned;
    });

    assertCallableWithOkAndResult(callable.getLastResult(), returned);
  }

  @Test
  @Timeout(2)
  void consumer_in50_OK() {
    int input = 50;
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, in -> {}, input);

    assertRunnableResultWithOk(result);
  }

  @Test
  @Timeout(2)
  void consumer_in50_OK_withResultConsumer() {
    int input = 50;
    AtomicBoolean called = new AtomicBoolean(false);
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, res -> called.set(true), in -> {}, input);

    Assertions.assertTrue(called.get());
    assertRunnableResultWithOk(result);
  }

  @Test
  @Timeout(2)
  void consumer_in50_out50_OK_submit() {
    int input = 50;
    WatchableConsumerForTest function = WatchableConsumerForTest.submitWatchable(watchdogFactory, 1000, in -> {}, input);

    assertRunnableResultWithOk(function.getLastResult());
  }

  @Test
  @Timeout(2)
  void function_in50_out50_OK() {
    int input = 50;
    TaskResult<Integer> result = watchdogFactory.waitForCompletion(1000, in -> in, input);

    assertCallableWithOkAndResult(result, input);
  }

  @Test
  @Timeout(2)
  void function_in50_out50_OK_withResultConsumer() {
    int input = 50;
    AtomicBoolean called = new AtomicBoolean(false);
    TaskResult<Integer> result = watchdogFactory.waitForCompletion(1000, res -> called.set(true), in -> in, input);

    Assertions.assertTrue(called.get());
    assertCallableWithOkAndResult(result, input);
  }

  @Test
  @Timeout(2)
  void function_in50_out50_OK_submit() {
    int input = 50;
    WatchableFunctionForTest function = WatchableFunctionForTest.submitWatchable(watchdogFactory,1000, in -> in, input);

    assertCallableWithOkAndResult(function.getLastResult(), input);
  }

  @Test
  @Timeout(2)
  void runnable_TIMEOUT() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {
      Thread.sleep(4000);
    });

    assertTimeout(result);
  }

  @Test
  @Timeout(2)
  void runnable_TIMEOUT_submit() {
    WatchableRunnableForTest runnable = WatchableRunnableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      Thread.sleep(4000);
    });

    assertTimeout(runnable.getLastResult());
  }

  @Test
  @Timeout(2)
  void callable_TIMEOUT() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {
      Thread.sleep(4000);
      return null;
    });

    assertTimeout(result);
  }

  @Test
  @Timeout(2)
  void callable_TIMEOUT_submit() {
    WatchableCallableForTest runnable = WatchableCallableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      Thread.sleep(4000);
      return null;
    });

    assertTimeout(runnable.getLastResult());
  }

  @Test
  @Timeout(2)
  void runnable_runtimeException_ERROR() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {
      throw new RuntimeException();
    });

    assertExecutionException(result);
    Assertions.assertTrue(result.getErrorReason() instanceof RuntimeException);
  }

  @Test
  @Timeout(2)
  void runnable_nullPointerException_ERROR_submit() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {
      Object object = null;
      String s = object.toString();
    });

    assertNullPointerException(result);
  }

  @Test
  @Timeout(2)
  void runnable_nullPointerException_ERROR() {
    assertNullPointerException(WatchableRunnableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      Object object = null;
      String s = object.toString();
    }).getLastResult());
  }

  @Test
  @Timeout(2)
  void callable_nullPointerException_ERROR() {
    TaskResult<String> result = watchdogFactory.waitForCompletion(1000, () -> {
      Object object = null;
      return object.toString();
    });

    assertNullPointerException(result);
  }

  @Test
  @Timeout(2)
  void callable_nullPointerException_ERROR_submit() {
    assertNullPointerException(WatchableCallableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      Object object = null;
      String s = object.toString();
      return 50;
    }).getLastResult());
  }

  @Test
  @Timeout(2)
  void runnable_testException_ERROR() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {
      Thread.sleep(500);
      throw new TestException();
    });

    assertErrorWithTestException(result);
  }

  @Test
  @Timeout(2)
  void runnable_testException_ERROR_submit() {
    WatchableCallableForTest callable = WatchableCallableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      Thread.sleep(500);
      throw new TestException();
    });

    assertErrorWithTestException(callable.getLastResult());
  }

  @Test
  void runnable_userTriesEverythingToSabotageTheTimeout_TIMEOUT() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, new Sabotage());

    assertTimeout(result);
  }

  private static class Sabotage implements WatchableRunnable {
    @Override
    public void run() {
      try {
        int i = 1;
        while (i > 0) {
          i++;
          if (i >= 100) {
            i = 1;
          }
        }
      } catch (Throwable throwable) {
        run();
      }
    }

    @Override
    public Object call() throws Exception {
      try {
        return WatchableRunnable.super.call();
      } catch (Throwable throwable) {
        return call();
      }
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

  private void assertErrorWithTestException(TaskResult<?> result) {
    assertExecutionException(result);
    Assertions.assertTrue(result.getErrorReason() instanceof TestException);
  }

  private void assertError(TaskResult<?> result) {
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.hasError());
    Assertions.assertEquals(ResultCode.ERROR, result.getCode());
    Assertions.assertNotNull(result.getErrorReason());
    Assertions.assertNull(result.getResult());
  }

  private void assertExecutionException(TaskResult<?> result) {
    assertError(result);
    Assertions.assertNotNull(result.getExecutionException());
    Assertions.assertNotNull(result.getExecutionException().getCause());
    Assertions.assertEquals(result.getExecutionException().getCause(), result.getErrorReason());
  }

  private void assertNullPointerException(TaskResult<?> result) {
    assertExecutionException(result);
    Assertions.assertTrue(result.getErrorReason() instanceof NullPointerException);
  }

  private void assertRunnableResultWithOk(TaskResult<?> result) {
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.hasError());
    Assertions.assertEquals(ResultCode.OK, result.getCode());
    Assertions.assertNull(result.getErrorReason());
    // assert null => runnable!
    Assertions.assertNull(result.getResult());
  }

  private void assertCallableWithOkAndResult(TaskResult<?> result, int expected) {
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.hasError());
    Assertions.assertEquals(ResultCode.OK, result.getCode());
    Assertions.assertNull(result.getErrorReason());
    Assertions.assertNotNull(result.getResult());
    Assertions.assertEquals(expected, result.getResult());
  }

}
