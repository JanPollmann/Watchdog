package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.ExceptionRunnable;
import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.testsupport.ResultCounter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class WatchdogFactoryWithExecutorServiceTest {

  @Test
  @Timeout(2)
  void runnable_endlessLoop_TIMEOUT() throws InterruptedException {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();

    assertTimeout(watchdogFactory.waitForCompletion(1000, Watchable.builder(() -> {
      while (true) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
      }
    }).build()));
  }

  @Test
  @Timeout(2)
  void nonBlocking_runnable_endlessLoop_TIMEOUT() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    ResultCounter<Object> resultCounter = new ResultCounter<>();

    assertTimeout(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(() -> {
      while (true) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
      }
    })));
  }

  @Test
  @Timeout(2)
  void callable_endlessLoop_TIMEOUT() throws InterruptedException {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();

    assertTimeout(watchdogFactory.waitForCompletion(1000, Watchable.builder(() -> {
      boolean continueLoop = true;
      while (continueLoop) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
      }
      return null;
    }).build()));
  }

  @Test
  @Timeout(2)
  void nonBlocking_callable_endlessLoop_TIMEOUT() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    ResultCounter<Object> resultCounter = new ResultCounter<>();

    assertTimeout(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(() -> {
      boolean continueLoop = true;
      while (continueLoop) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
      }
      return null;
    })));
  }

  @Test
  @Timeout(8)
  void runnable_endlessLoop_TIMEOUT_repeated() throws InterruptedException {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    Watchable<Object> runnable = Watchable.builder(() -> {
      while (true) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
      }
    }).build();

    assertTimeout(watchdogFactory.waitForCompletion(1000, runnable));

    runnable = Watchable.builder(() -> {
      int i = 1;
      while (i > 0) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        i++;
        if (i == 100) {
          i = 1;
        }
      }
    }).build();

    assertTimeout(watchdogFactory.waitForCompletion(1000, runnable));

    assertTimeout(watchdogFactory.waitForCompletion(1000, runnable));
  }

  @Test
  @Timeout(8)
  void nonBlocking_runnable_endlessLoop_TIMEOUT_repeated() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    ResultCounter<Object> resultCounter = new ResultCounter<>();
    ExceptionRunnable runnable = () -> {
      while (true) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
      }
    };

    assertTimeout(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(runnable)));

    runnable = () -> {
      int i = 1;
      while (i > 0) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        i++;
        if (i == 100) {
          i = 1;
        }
      }
    };

    assertTimeout(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(runnable)));

    assertTimeout(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(runnable)));
  }

  @Test
  @Timeout(8)
  void callable_endlessLoop_TIMEOUT_repeated() throws InterruptedException {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    Callable<?> callable = () -> {
      boolean continueLoop = true;
      while (continueLoop) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
      }
      return null;
    };

    assertTimeout(watchdogFactory.waitForCompletion(1000, Watchable.builder(callable).build()));

    callable = () -> {
      int i = 1;
      while (i > 0) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        i++;
        if (i == 100) {
          i = 1;
        }
      }
      return null;
    };

    assertTimeout(watchdogFactory.waitForCompletion(1000, Watchable.builder(callable).build()));

    assertTimeout(watchdogFactory.waitForCompletion(1000, Watchable.builder(callable).build()));
  }

  @Test
  @Timeout(8)
  void nonBlocking_callable_endlessLoop_TIMEOUT_repeated() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    ResultCounter<Integer> resultCounter = new ResultCounter<>();
    Callable<Integer> callable = () -> {
      boolean continueLoop = true;
      while (continueLoop) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
      }
      return null;
    };

    assertTimeout(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(callable)));

    callable = () -> {
      int i = 1;
      while (i > 0) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        i++;
        if (i == 100) {
          i = 1;
        }
      }
      return null;
    };

    assertTimeout(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(callable)));

    assertTimeout(resultCounter.submit(watchdogFactory, 1000, Watchable.builder(callable)));
  }

  @Test
  @Timeout(8)
  void parallel_runnable_endlessLoop_TIMEOUT_repeated() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    ResultCounter<Object> resultCounter = new ResultCounter<>(this::assertTimeout);
    ExceptionRunnable runnable = () -> {
      int i = 1;
      while (i > 0) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        i++;
        if (i == 100) {
          i = 1;
        }
      }
    };

    List<Future<?>> futures = new ArrayList<>();
    futures.add(watchdogFactory.submitFunctionCall(1000, Watchable.builder(runnable).build()));
    futures.add(watchdogFactory.submitFunctionCall(1000, Watchable.builder(runnable).build()));
    futures.add(watchdogFactory.submitFunctionCall(1000, Watchable.builder(runnable).build()));

    while (!futuresFinished(futures)) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Assertions.fail(e);
      }
    }
  }

  @Test
  @Timeout(8)
  void parallel_callable_endlessLoop_TIMEOUT_repeated() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    ResultCounter<Integer> resultCounter = new ResultCounter<>(this::assertTimeout);
    Callable<Integer> callable = () -> {
      int i = 1;
      while (i > 0) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        i++;
        if (i == 100) {
          i = 1;
        }
      }
      return null;
    };

    List<Future<?>> futures = new ArrayList<>();
    futures.add(watchdogFactory.submitFunctionCall(1000, Watchable.builder(callable).build()));
    futures.add(watchdogFactory.submitFunctionCall(1000, Watchable.builder(callable).build()));
    futures.add(watchdogFactory.submitFunctionCall(1000, Watchable.builder(callable).build()));

    while (!futuresFinished(futures)) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Assertions.fail(e);
      }
    }
  }

  private void assertTimeout(TaskResult<?> result) {
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.hasError());
    Assertions.assertEquals(ResultCode.TIMEOUT, result.getCode());
    Assertions.assertNotNull(result.getErrorReason());
    Assertions.assertTrue(result.getErrorReason() instanceof TimeoutException);
    Assertions.assertNull(result.getResult());
  }

  private boolean futuresFinished(List<Future<?>> futures) {
    for(Future<?> future : futures) {
      if (!future.isDone()) {
        return false;
      }
    }
    return true;
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
