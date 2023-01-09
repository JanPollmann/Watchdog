package de.pollmann.watchdog.tester.tester;

import de.pollmann.watchdog.ResultCode;
import de.pollmann.watchdog.TaskResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public abstract class CountableTest implements Tester {

  protected AtomicLong executionCounter = new AtomicLong(0);
  protected AtomicLong taskFinishedCounter = new AtomicLong(0);
  protected long startExecutionCounter;

  @Override
  public void startTest() throws Exception{
    startExecutionCounter = executionCounter.get();
  }

  @Override
  public void finishTest() throws Exception {

  }

  protected void validate(long expectedCounter) throws ValidationException {
    if (expectedCounter != executionCounter.get() ||expectedCounter != taskFinishedCounter.get()) {
      throw new ValidationException(this, "Counter missmatch!");
    }
  }

  protected void taskFinished(TaskResult<?> result, ResultCode expected) {
    if (result.getCode() == expected) {
      taskFinishedCounter.incrementAndGet();
    }
  }

  protected void finishFuture(Future<?> future) throws InterruptedException, ExecutionException {
    future.get();
  }

}
