package de.pollmann.watchdog;

import de.pollmann.watchdog.testsupport.TestException;
import de.pollmann.watchdog.testsupport.WatchableCallableForTest;
import de.pollmann.watchdog.testsupport.WatchableRunnableForTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeoutException;

public class WatchdogFactoryTest {

  private final WatchdogFactory watchdogFactory = new WatchdogFactory();

  @Test
  @Timeout(2)
  void watchdogOneSecond_noDelay_OK() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {});

    assertRunnableResultWithOk(result);
  }

  @Test
  @Timeout(2)
  void nonBlocking_watchdogOneSecond_noDelay_OK() throws InterruptedException {
    WatchableRunnableForTest runnable = WatchableRunnableForTest.submitWatchable(watchdogFactory, 1000, () -> {});

    assertRunnableResultWithOk(runnable.getLastResult());
  }

  private void assertRunnableResultWithOk(TaskResult<?> result) {
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.hasError());
    Assertions.assertEquals(ResultCode.OK, result.getCode());
    Assertions.assertNull(result.getErrorReason());
    // assert null => runnable!
    Assertions.assertNull(result.getResult());
  }

  @Test
  @Timeout(2)
  void runnable_userException_throwable_resultsERROR() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {
      throw new RuntimeException();
    });

    assertExecutionException(result);
    Assertions.assertTrue(result.getErrorReason() instanceof RuntimeException);
  }

  @Test
  @Timeout(2)
  void runnable_userException_nullPointer_resultsERROR() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {
      Object object = null;
      String s = object.toString();
    });

    assertNullPointerException(result);
  }

  @Test
  @Timeout(2)
  void callable_userException_nullPointer_resultsERROR() {
    TaskResult<String> result = watchdogFactory.waitForCompletion(1000, () -> {
      Object object = null;
      return object.toString();
    });

    assertNullPointerException(result);
  }

  @Test
  @Timeout(2)
  void nonBlocking_runnable_userException_nullPointer_resultsERROR() {
    assertNullPointerException(WatchableRunnableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      Object object = null;
      String s = object.toString();
    }).getLastResult());
  }

  @Test
  @Timeout(2)
  void nonBlocking_callable_userException_nullPointer_resultsERROR() {
    assertNullPointerException(WatchableCallableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      Object object = null;
      String s = object.toString();
      return 50;
    }).getLastResult());
  }

  private void assertNullPointerException(TaskResult<?> result) {
    assertExecutionException(result);
    Assertions.assertTrue(result.getErrorReason() instanceof NullPointerException);
  }


  @Test
  @Timeout(2)
  void watchdogOneSecond_delay500ms_return50_OK() {
    int returned = 50;
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {
      Thread.sleep(500);
      return returned;
    });

    assertCallableWithOkAndResult(result, returned);
  }

  @Test
  @Timeout(2)
  void nonBlocking_watchdogOneSecond_delay500ms_return50_OK() {
    int returned = 50;
    WatchableCallableForTest callable = WatchableCallableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      Thread.sleep(500);
      return returned;
    });

    assertCallableWithOkAndResult(callable.getLastResult(), returned);
  }

  private void assertCallableWithOkAndResult(TaskResult<?> result, int expected) {
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.hasError());
    Assertions.assertEquals(ResultCode.OK, result.getCode());
    Assertions.assertNull(result.getErrorReason());
    Assertions.assertNotNull(result.getResult());
    Assertions.assertEquals(expected, result.getResult());
  }

  @Test
  @Timeout(2)
  void watchdogOneSecond_sleep4seconds_TIMEOUT() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {
      Thread.sleep(4000);
    });

    assertTimeout(result);
  }

  @Test
  @Timeout(2)
  void nonBlocking_watchdogOneSecond_sleep4seconds_TIMEOUT() {
    WatchableRunnableForTest runnable = WatchableRunnableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      Thread.sleep(4000);
    });

    assertTimeout(runnable.getLastResult());
  }

  private void assertTimeout(TaskResult<?> result) {
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.hasError());
    Assertions.assertEquals(ResultCode.TIMEOUT, result.getCode());
    Assertions.assertNotNull(result.getErrorReason());
    Assertions.assertNull(result.getResult());
    Assertions.assertTrue(result.getErrorReason() instanceof TimeoutException);
  }

  @Test
  @Timeout(2)
  void watchdogOneSecond_sleep500ms_throwException_ERROR() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, () -> {
      Thread.sleep(500);
      throw new TestException();
    });

    assertErrorWithTestException(result);
  }

  @Test
  @Timeout(2)
  void nonBlocking_watchdogOneSecond_sleep500ms_throwException_ERROR() {
    WatchableCallableForTest callable = WatchableCallableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      Thread.sleep(500);
      throw new TestException();
    });

    assertErrorWithTestException(callable.getLastResult());
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

}
