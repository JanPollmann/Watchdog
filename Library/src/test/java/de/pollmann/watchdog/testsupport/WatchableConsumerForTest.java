package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.WatchableConsumer;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.Future;
import java.util.function.Consumer;

public class WatchableConsumerForTest extends WatchableConsumer<Integer> implements StoreResult<Object> {

  private TaskResult<Object> lastResult = null;
  private int finishedCounter = 0;

  public WatchableConsumerForTest(Consumer<TaskResult<Object>>resultConsumer, Consumer<Integer> consumer, Integer input) {
    super(resultConsumer, consumer, input);
  }

  public WatchableConsumerForTest(Consumer<Integer> consumer, Integer input) {
    super(consumer, input);
  }

  @Override
  public TaskResult<Object> getLastResult() {
    return lastResult;
  }

  @Override
  public int getFinishedCounter() {
    return finishedCounter;
  }

  @Override
  public Consumer<TaskResult<Object>> getResultConsumer() {
    return this::internResultConsumer;
  }

  private void internResultConsumer(TaskResult<Object> result) {
    if (super.getResultConsumer() != null) {
      super.getResultConsumer().accept(result);
    }
    lastResult = result;
    finishedCounter++;
  }

  public static WatchableConsumerForTest submitWatchable(WatchdogFactory factory, long timeoutInMs, Consumer<Integer> consumer, Integer input) {
    WatchableConsumerForTest watchableConsumerForTest = new WatchableConsumerForTest(consumer, input);
    submitWatchable(factory, timeoutInMs, watchableConsumerForTest);
    return watchableConsumerForTest;
  }

  public static void submitWatchable(WatchdogFactory factory, long timeoutInMs, WatchableConsumerForTest watchableConsumerForTest) {
    Future<?> watched = factory.submitFunctionCall(timeoutInMs, watchableConsumerForTest);
    while (!watched.isDone()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Assertions.fail(e);
      }
    }
  }

}
