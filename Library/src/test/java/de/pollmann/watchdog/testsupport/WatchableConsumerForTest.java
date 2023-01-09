package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.WatchableConsumer;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.Future;
import java.util.function.Consumer;

public class WatchableConsumerForTest extends WatchableConsumer<Integer> implements WatchableForTest<Object> {

  private final Consumer<Integer> consumer;

  private TaskResult<Object> lastResult = null;

  public WatchableConsumerForTest(Consumer<Integer> consumer) {
    this.consumer = consumer;
  }

  @Override
  public TaskResult<Object> getLastResult() {
    return lastResult;
  }

  @Override
  public void finishedWithResult(TaskResult<Object> result) {
    super.finishedWithResult(result);
    lastResult = result;
  }

  @Override
  public void accept(Integer input) throws Exception {
    consumer.accept(input);
  }

  public static WatchableConsumerForTest submitWatchable(WatchdogFactory factory, long timeoutInMs, Consumer<Integer> consumer, Integer input) {
    WatchableConsumerForTest watchableConsumerForTest = new WatchableConsumerForTest(consumer);
    Future<?> watched = factory.submitFunctionCall(timeoutInMs, watchableConsumerForTest, input);
    while (!watched.isDone()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Assertions.fail(e);
      }
    }
    return watchableConsumerForTest;
  }
}
