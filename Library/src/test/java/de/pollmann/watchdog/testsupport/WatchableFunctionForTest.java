package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.WatchableFunction;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.Future;
import java.util.function.Function;

public class WatchableFunctionForTest extends WatchableFunction<Integer, Integer> implements WatchableForTest<Integer> {

  private final Function<Integer,Integer> function;

  private TaskResult<Integer> lastResult = null;

  public WatchableFunctionForTest(Function<Integer,Integer> function, Integer input) {
    super(input);
    this.function = function;
  }

  @Override
  public Integer apply(Integer integer) {
    return function.apply(integer);
  }

  @Override
  public WatchableFunctionForTest clone(Integer newInput) {
    return new WatchableFunctionForTest(function, newInput);
  }

  @Override
  public TaskResult<Integer> getLastResult() {
    return lastResult;
  }

  @Override
  public void finishedWithResult(TaskResult<Integer> result) {
    super.finishedWithResult(result);
    lastResult = result;
  }

  public static WatchableFunctionForTest submitWatchable(WatchdogFactory factory, long timeoutInMs, Function<Integer,Integer> function, Integer input) {
    WatchableFunctionForTest watchableFunctionForTest = new WatchableFunctionForTest(function, input);
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
