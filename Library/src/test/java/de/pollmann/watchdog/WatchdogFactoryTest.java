package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.ExceptionFunction;
import de.pollmann.watchdog.tasks.ExceptionRunnable;
import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.testsupport.ResultCounter;
import de.pollmann.watchdog.testsupport.TestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

class WatchdogFactoryTest {

  private final WatchdogFactory watchdogFactory = new WatchdogFactory();

  @Test
  @Timeout(2)
  void runnable_OK() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, Watchable.builder(() -> {}).build());

    assertRunnableResultWithOk(result);
  }

  @Test
  @Timeout(2)
  void runnable_noTimeout_OK() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(0, Watchable.builder(() -> {}).build());

    assertRunnableResultWithOk(result);
  }

  @Test
  @Timeout(2)
  void runnable_OK_submit() {
    ResultCounter<Object> resultCounter = new ResultCounter<>();
    assertRunnableResultWithOk(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(() -> {})));
  }

  @Test
  @Timeout(2)
  void callable_out50_OK() {
    int returned = 50;
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, Watchable.builder(() -> {
      Thread.sleep(500);
      return returned;
    }).build());

    assertCallableWithOkAndResult(result, returned);
  }

  @Test
  @Timeout(2)
  void callable_out50_OK_submit() {
    int returned = 50;
    ResultCounter<Integer> resultCounter = new ResultCounter<>();
    Callable<Integer> consumer = () -> {
      Thread.sleep(500);
      return returned;
    };
    assertCallableWithOkAndResult(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(consumer)), returned);
  }

  @Test
  @Timeout(2)
  void consumer_in50_OK() {
    int input = 50;
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, Watchable.builder(in -> {})
      .withInput(input)
      .build());

    assertRunnableResultWithOk(result);
  }

  @Test
  @Timeout(2)
  void consumer_in50_OK_withResultConsumer() {
    int input = 50;
    AtomicBoolean called = new AtomicBoolean(false);
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, Watchable.builder(in -> {})
      .withInput(input)
      .withResultConsumer(res -> called.set(true))
      .build());

    Assertions.assertTrue(called.get());
    assertRunnableResultWithOk(result);
  }

  @Test
  @Timeout(2)
  void consumer_in50_out50_OK_submit() {
    int input = 50;
    ResultCounter<Object> resultCounter = new ResultCounter<>();
    assertRunnableResultWithOk(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(in -> {})
            .withInput(input)
    ));
  }

  @Test
  @Timeout(2)
  void function_in50_out50_OK() {
    int input = 50;
    TaskResult<Integer> result = watchdogFactory.waitForCompletion(1000, Watchable.builder((ExceptionFunction<Integer, Integer>) in -> in)
      .withInput(input)
      .build()
    );

    assertCallableWithOkAndResult(result, input);
  }

  @Test
  @Timeout(2)
  void function_in50_out50_OK_withResultConsumer() {
    int input = 50;
    AtomicBoolean called = new AtomicBoolean(false);
    TaskResult<Integer> result = watchdogFactory.waitForCompletion(1000, Watchable.builder((ExceptionFunction<Integer, Integer>) in -> in)
      .withInput(input)
      .withResultConsumer(res -> called.set(true))
      .build()
    );

    Assertions.assertTrue(called.get());
    assertCallableWithOkAndResult(result, input);
  }

  @Test
  @Timeout(2)
  void function_in50_out50_OK_submit() {
    int input = 50;
    ResultCounter<Integer> resultCounter = new ResultCounter<>();
    assertCallableWithOkAndResult(resultCounter.submit(watchdogFactory,1000, Watchable.builder((ExceptionFunction<Integer, Integer>) in -> in)
      .withInput(input)), input);
  }

  @Test
  @Timeout(2)
  void runnable_TIMEOUT() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, Watchable.builder(() -> {
      Thread.sleep(4000);
    }).build());

    assertTimeout(result);
  }

  @Test
  @Timeout(2)
  void runnable_TIMEOUT_submit() {
    ResultCounter<Object> resultCounter = new ResultCounter<>();

    assertTimeout(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(() -> {
      Thread.sleep(4000);
    })));
  }

  @Test
  @Timeout(2)
  void callable_TIMEOUT() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, Watchable.builder(() -> {
      Thread.sleep(4000);
      return null;
    }).build());

    assertTimeout(result);
  }

  @Test
  @Timeout(2)
  void callable_TIMEOUT_submit() {
    ResultCounter<Object> resultCounter = new ResultCounter<>();
    assertTimeout(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(() -> {
      Thread.sleep(4000);
      return null;
    })));
  }

  @Test
  @Timeout(2)
  void runnable_runtimeException_ERROR() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, Watchable.builder(() -> {
      throw new RuntimeException();
    }).build());

    assertExecutionException(result);
    Assertions.assertTrue(result.getErrorReason() instanceof RuntimeException);
  }

  @Test
  @Timeout(2)
  void runnable_nullPointerException_ERROR_submit() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, Watchable.builder(() -> {
      Object object = null;
      String s = object.toString();
    }).build());

    assertNullPointerException(result);
  }

  @Test
  @Timeout(2)
  void runnable_nullPointerException_ERROR() {
    ResultCounter<Object> resultCounter = new ResultCounter<>();
    assertNullPointerException(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(() -> {
      Object object = null;
      String s = object.toString();
    })));
  }

  @Test
  @Timeout(2)
  void callable_nullPointerException_ERROR() {
    TaskResult<String> result = watchdogFactory.waitForCompletion(1000, Watchable.builder(() -> {
      Object object = null;
      return object.toString();
    }).build());

    assertNullPointerException(result);
  }

  @Test
  @Timeout(2)
  void callable_nullPointerException_ERROR_submit() {
    ResultCounter<Object> resultCounter = new ResultCounter<>();
    assertNullPointerException(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(() -> {
      Object object = null;
      String s = object.toString();
      return 50;
    })));
  }

  @Test
  @Timeout(2)
  void runnable_testException_ERROR() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, Watchable.builder(() -> {
      Thread.sleep(500);
      throw new TestException();
    }).build());

    assertErrorWithTestException(result);
  }

  @Test
  @Timeout(2)
  void runnable_testException_ERROR_submit() {
    ResultCounter<Object> resultCounter = new ResultCounter<>();

    assertErrorWithTestException(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(() -> {
      Thread.sleep(500);
      throw new TestException();
    })));
  }

  @Test
  void runnable_userTriesEverythingToSabotageTheTimeout_TIMEOUT() {
    TaskResult<?> result = watchdogFactory.waitForCompletion(1000, createSabotage());

    assertTimeout(result);
  }

  private Watchable<Object> createSabotage() {
    return Watchable.builder(new Sabotage()).build();
  }

  private static class Sabotage implements ExceptionRunnable {
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
