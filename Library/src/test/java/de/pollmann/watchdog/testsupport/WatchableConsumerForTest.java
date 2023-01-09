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

  private WatchableConsumerForTest(ConsumerProxy<TaskResult<Object>> resultConsumer, Consumer<Integer> consumer, Integer input) {
    super(resultConsumer, consumer, input);
    Consumer<TaskResult<Object>> consumerProxy = resultConsumer.getConsumer();
    if (consumer == null) {
      resultConsumer.setConsumer(this::internResultConsumer);
    } else {
      resultConsumer.setConsumer(result -> {
        this.internResultConsumer(result);
        consumerProxy.accept(result);
      });
    }
  }

  public WatchableConsumerForTest(Consumer<TaskResult<Object>> resultConsumer, Consumer<Integer> consumer, Integer input) {
    this(new ConsumerProxy<>(resultConsumer), consumer, input);
  }

  public WatchableConsumerForTest(Consumer<Integer> consumer, Integer input) {
    this(new ConsumerProxy<>(), consumer, input);
  }

  @Override
  public TaskResult<Object> getLastResult() {
    return lastResult;
  }

  @Override
  public int getFinishedCounter() {
    return finishedCounter;
  }

  private void internResultConsumer(TaskResult<Object> result) {
    lastResult = result;
    finishedCounter++;
  }

  public static WatchableConsumerForTest submitWatchable(WatchdogFactory factory, long timeoutInMs, Consumer<TaskResult<Object>> resultConsumer, Consumer<Integer> consumer, Integer input) {
    WatchableConsumerForTest watchableConsumerForTest = new WatchableConsumerForTest(resultConsumer, consumer, input);
    Future<?> watched = factory.submitFunctionCall(timeoutInMs, watchableConsumerForTest);
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
