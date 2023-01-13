package de.pollmann.watchdog.tester.tester;

import de.pollmann.watchdog.RepeatableTaskWithoutInput;
import de.pollmann.watchdog.ResultCode;
import de.pollmann.watchdog.WatchableOptions;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.Watchable;

import java.util.concurrent.Future;

public class RunnableOkTester extends CountableTest {

  private final RepeatableTaskWithoutInput<Object> repeatable;

  private Future<?> future;

  public RunnableOkTester(WatchdogFactory factory) {
    super();
    repeatable = factory.createRepeated(WatchableOptions.builder(500).build(), Watchable.builder(this::run)
      .withResultConsumer(result -> this.taskFinished(result, ResultCode.OK))
      .build()
    );
  }

  public void run() {
    executionCounter.incrementAndGet();
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
