package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.WatchableFunction;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

public class WatchableFunctionForTest extends WatchableFunction<Integer, Integer> implements StoreResult<Integer> {

  private TaskResult<Integer> lastResult = null;
  private int finishedCounter = 0;

  private WatchableFunctionForTest(ConsumerProxy<TaskResult<Integer>> resultConsumer, Function<Integer, Integer> function, Integer input) {
    super(resultConsumer, function, input);
    Consumer<TaskResult<Integer>> consumer = resultConsumer.getConsumer();
    if (consumer == null) {
      resultConsumer.setConsumer(this::internResultConsumer);
    } else {
      resultConsumer.setConsumer(result -> {
        this.internResultConsumer(result);
        consumer.accept(result);
      });
    }
  }

  public WatchableFunctionForTest(Consumer<TaskResult<Integer>> resultConsumer, Function<Integer, Integer> function, Integer input) {
    this(new ConsumerProxy<>(resultConsumer), function, input);
  }

  public WatchableFunctionForTest(Function<Integer, Integer> function, Integer input) {
    this(new ConsumerProxy<>(), function, input);
  }

  @Override
  public TaskResult<Integer> getLastResult() {
    return lastResult;
  }

  @Override
  public int getFinishedCounter() {
    return finishedCounter;
  }

  private void internResultConsumer(TaskResult<Integer> result) {
    lastResult = result;
    finishedCounter++;
  }

  public static WatchableFunctionForTest submitWatchable(WatchdogFactory factory, long timeoutInMs, Consumer<TaskResult<Integer>> resultConsumer, Function<Integer,Integer> function, Integer input) {
    WatchableFunctionForTest watchableFunctionForTest = new WatchableFunctionForTest(resultConsumer, function, input);
    Future<?> watched = factory.submitFunctionCall(timeoutInMs, watchableFunctionForTest);
    while (!watched.isDone()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Assertions.fail(e);
      }
    }
    return watchableFunctionForTest;
  }
}
