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

  public WatchableFunctionForTest(Consumer<TaskResult<Integer>> resultConsumer, Function<Integer, Integer> function, Integer input) {
    super(resultConsumer, function, input);
  }

  public WatchableFunctionForTest(Function<Integer, Integer> function, Integer input) {
    super(function, input);
  }

  @Override
  public TaskResult<Integer> getLastResult() {
    return lastResult;
  }

  @Override
  public int getFinishedCounter() {
    return finishedCounter;
  }

  @Override
  public Consumer<TaskResult<Integer>> getResultConsumer() {
    return this::internResultConsumer;
  }

  private void internResultConsumer(TaskResult<Integer> result) {
    if (super.getResultConsumer() != null) {
      super.getResultConsumer().accept(result);
    }
    lastResult = result;
    finishedCounter++;
  }

  public static WatchableFunctionForTest submitWatchable(WatchdogFactory factory, long timeoutInMs, Function<Integer,Integer> function, Integer input) {
    WatchableFunctionForTest watchableFunctionForTest = new WatchableFunctionForTest(function, input);
    submitWatchable(factory, timeoutInMs, watchableFunctionForTest);
    return watchableFunctionForTest;
  }

  public static void submitWatchable(WatchdogFactory factory, long timeoutInMs, WatchableFunctionForTest watchableFunctionForTest) {
    Future<?> watched = factory.submitFunctionCall(timeoutInMs, watchableFunctionForTest);
    while (!watched.isDone()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Assertions.fail(e);
      }
    }
  }
}
