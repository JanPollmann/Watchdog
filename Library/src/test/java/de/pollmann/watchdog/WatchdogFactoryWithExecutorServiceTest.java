package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.WatchableCallable;
import de.pollmann.watchdog.tasks.WatchableRunnable;
import de.pollmann.watchdog.testsupport.WatchableCallableForTest;
import de.pollmann.watchdog.testsupport.WatchableRunnableForTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class WatchdogFactoryWithExecutorServiceTest {

  @Test
  @Timeout(2)
  void runnable_endlessLoop_TIMEOUT() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();

    assertTimeout(watchdogFactory.waitForCompletion(1000, () -> {
      while (true) {
        // well ...
      }
    }));
  }

  @Test
  @Timeout(2)
  void nonBlocking_runnable_endlessLoop_TIMEOUT() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();

    assertTimeout(WatchableRunnableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      while (true) {
        // well ...
      }
    }).getLastResult());
  }

  @Test
  @Timeout(2)
  void callable_endlessLoop_TIMEOUT() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();

    assertTimeout(watchdogFactory.waitForCompletion(1000, () -> {
      boolean continueLoop = true;
      while (continueLoop) {
        // well ...
      }
      return null;
    }));
  }

  @Test
  @Timeout(2)
  void nonBlocking_callable_endlessLoop_TIMEOUT() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();

    assertTimeout(WatchableCallableForTest.submitWatchable(watchdogFactory, 1000, () -> {
      boolean continueLoop = true;
      while (continueLoop) {
        // well ...
      }
      return null;
    }).getLastResult());
  }

  @Test
  @Timeout(4)
  void runnable_endlessLoop_TIMEOUT_repeated() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    WatchableRunnable runnable = () -> {
      while (true) {
        // well ...
      }
    };

    assertTimeout(watchdogFactory.waitForCompletion(1000, runnable));

    runnable = () -> {
      int i = 1;
      while (i > 0) {
        i++;
        if (i == 100) {
          i = 1;
        }
      }
    };

    assertTimeout(watchdogFactory.waitForCompletion(1000, runnable));

    assertTimeout(watchdogFactory.waitForCompletion(1000, runnable));
  }

  @Test
  @Timeout(4)
  void nonBlocking_runnable_endlessLoop_TIMEOUT_repeated() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    WatchableRunnable runnable = () -> {
      while (true) {
        // well ...
      }
    };

    assertTimeout(WatchableRunnableForTest.submitWatchable(watchdogFactory, 1000, runnable).getLastResult());

    runnable = () -> {
      int i = 1;
      while (i > 0) {
        i++;
        if (i == 100) {
          i = 1;
        }
      }
    };

    assertTimeout(WatchableRunnableForTest.submitWatchable(watchdogFactory, 1000, runnable).getLastResult());

    assertTimeout(WatchableRunnableForTest.submitWatchable(watchdogFactory, 1000, runnable).getLastResult());
  }

  @Test
  @Timeout(4)
  void callable_endlessLoop_TIMEOUT_repeated() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    WatchableCallable<?> callable = () -> {
      boolean continueLoop = true;
      while (continueLoop) {
        // well ...
      }
      return null;
    };

    assertTimeout(watchdogFactory.waitForCompletion(1000, callable));

    callable = () -> {
      int i = 1;
      while (i > 0) {
        i++;
        if (i == 100) {
          i = 1;
        }
      }
      return null;
    };

    assertTimeout(watchdogFactory.waitForCompletion(1000, callable));

    assertTimeout(watchdogFactory.waitForCompletion(1000, callable));
  }

  @Test
  @Timeout(4)
  void nonBlocking_callable_endlessLoop_TIMEOUT_repeated() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    Callable<Integer> callable = () -> {
      boolean continueLoop = true;
      while (continueLoop) {
        // well ...
      }
      return null;
    };

    assertTimeout(WatchableCallableForTest.submitWatchable(watchdogFactory, 1000, callable).getLastResult());

    callable = () -> {
      int i = 1;
      while (i > 0) {
        i++;
        if (i == 100) {
          i = 1;
        }
      }
      return null;
    };

    assertTimeout(WatchableCallableForTest.submitWatchable(watchdogFactory, 1000, callable).getLastResult());

    assertTimeout(WatchableCallableForTest.submitWatchable(watchdogFactory, 1000, callable).getLastResult());
  }

  @Test
  @Timeout(6)
  void parallel_runnable_endlessLoop_TIMEOUT_repeated() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    WatchableRunnableForTest runnable = new WatchableRunnableForTest(() -> {
      int i = 1;
      while (i > 0) {
        i++;
        if (i == 100) {
          i = 1;
        }
      }
    }) {
      @Override
      public void finishedWithResult(TaskResult<Object> result) {
        super.finishedWithResult(result);
        assertTimeout(result);
      }
    };

    List<Future<?>> futures = new ArrayList<>();
    futures.add(watchdogFactory.submitFunctionCall(1000, runnable));
    futures.add(watchdogFactory.submitFunctionCall(1000, runnable));
    futures.add(watchdogFactory.submitFunctionCall(1000, runnable));

    while (!futuresFinished(futures)) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Assertions.fail(e);
      }
    }
  }

  @Test
  @Timeout(6)
  void parallel_callable_endlessLoop_TIMEOUT_repeated() {
    WatchdogFactory watchdogFactory = withSingleThreadExecutor();
    WatchableCallableForTest callable = new WatchableCallableForTest(() -> {
      int i = 1;
      while (i > 0) {
        i++;
        if (i == 100) {
          i = 1;
        }
      }
      return null;
    }) {
      @Override
      public void finishedWithResult(TaskResult<Integer> result) {
        super.finishedWithResult(result);
        assertTimeout(result);
      }
    };

    List<Future<?>> futures = new ArrayList<>();
    futures.add(watchdogFactory.submitFunctionCall(1000, callable));
    futures.add(watchdogFactory.submitFunctionCall(1000, callable));
    futures.add(watchdogFactory.submitFunctionCall(1000, callable));

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
