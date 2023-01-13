package de.pollmann.watchdog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RepeatableTaskTest {

  @Test
  void terminate_works() {
    RepeatableTaskForTest repeatableTaskForTest = new RepeatableTaskForTest();
    assertIsNotTerminated(repeatableTaskForTest);

    repeatableTaskForTest.terminate();

    assertIsTerminated(repeatableTaskForTest);
  }

  @Test
  void shutdownWatchdog_terminate_works() {
    RepeatableTaskForTest repeatableTaskForTest = new RepeatableTaskForTest();
    assertIsNotTerminated(repeatableTaskForTest);

    repeatableTaskForTest.watchdogPool.shutdown();

    assertIsTerminated(repeatableTaskForTest);
  }

  @Test
  void shutdownWorker_terminate_works() {
    RepeatableTaskForTest repeatableTaskForTest = new RepeatableTaskForTest();
    assertIsNotTerminated(repeatableTaskForTest);

    repeatableTaskForTest.workerPool.shutdown();

    assertIsTerminated(repeatableTaskForTest);
  }

  private void assertIsNotTerminated(RepeatableTaskForTest repeatableTaskForTest) {
    Assertions.assertFalse(repeatableTaskForTest.isTerminated());
    Assertions.assertDoesNotThrow(repeatableTaskForTest::getWorkerIfAvailable);
  }

  private void assertIsTerminated(RepeatableTaskForTest repeatableTaskForTest) {
    Assertions.assertTrue(repeatableTaskForTest.isTerminated());
    Assertions.assertThrows(RepeatableTaskTerminatedException.class, repeatableTaskForTest::getWorkerIfAvailable);
  }

  private static class RepeatableTaskForTest extends RepeatableTask {

    public ExecutorService watchdogPool;
    public ExecutorService workerPool;

    public RepeatableTaskForTest(ExecutorService watchdogPool, ExecutorService workerPool) {
      super(new WatchdogWorker(
        watchdogPool,
        workerPool
      ), WatchableOptions.builder(0).build());
      this.watchdogPool = watchdogPool;
      this.workerPool = workerPool;
    }

    public RepeatableTaskForTest() {
      this(newSingleThreadExecutor("test:watchdog"), newSingleThreadExecutor("test:worker"));
    }

    private static ExecutorService newSingleThreadExecutor(String threadPrefix) {
      return Executors.newSingleThreadExecutor(WatchdogFactory.createDefaultThreadFactory(threadPrefix));
    }

  }
}
