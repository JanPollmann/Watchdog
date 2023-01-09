package de.pollmann.watchdog.tester.tester;

import de.pollmann.watchdog.RepeatableTaskWithoutInput;
import de.pollmann.watchdog.ResultCode;
import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.WatchableRunnable;

import java.util.concurrent.Future;
import java.util.function.Consumer;

public class RunnableOkTester extends CountableTest {

  private final RepeatableTaskWithoutInput<Object> repeatable;

  private Future<?> future;

  public RunnableOkTester(WatchdogFactory factory) {
    super();
    repeatable = factory.createRepeated(500, new WatchableRunnable() {
      @Override
      public void run() throws Exception {
        executionCounter.incrementAndGet();
      }

      @Override
      public Consumer<TaskResult<Object>> getResultConsumer() {
        return result -> taskFinished(result, ResultCode.OK);
      }
    });
  }

  @Override
  public void startTest() throws Exception{
    super.startTest();
    future = repeatable.submitFunctionCall();
    repeatable.waitForCompletion();
  }

  @Override
  public void finishTest() throws Exception{
    finishFuture(future);
    super.finishTest();
    validate(startExecutionCounter + 2);
  }

}
