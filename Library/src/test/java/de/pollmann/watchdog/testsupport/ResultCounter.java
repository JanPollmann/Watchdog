package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.*;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ResultCounter<OUT> implements StoreResult<OUT> {

  private final ResultConsumer<OUT> onResult;

  private TaskResult<OUT> lastResult = null;
  private int finishedCounter = 0;

  public ResultCounter(ResultConsumer<OUT> onResult) {
    this.onResult = onResult;
  }

  public ResultCounter() {
    this(null);
  }

  @Override
  public TaskResult<OUT> getLastResult() {
    return lastResult;
  }

  @Override
  public int getFinishedCounter() {
    return finishedCounter;
  }

  @Override
  public void accept(TaskResult<OUT> taskResult) throws InterruptedException {
    Assertions.assertNotNull(taskResult);
    Assertions.assertNotNull(taskResult.getCode());
    Assertions.assertNotNull(taskResult.getWatchable());
    Assertions.assertTrue(taskResult.getWatchable().stopped());
    lastResult = taskResult;
    finishedCounter++;
    if (onResult != null) {
      onResult.accept(taskResult);
    }
  }

  public <IN, WATCHABLE extends Watchable<OUT>> TaskResult<OUT> submit(WatchdogFactory watchdogFactory, long timeoutInMilliseconds, WatchableBuilder<IN, OUT, ?, WATCHABLE> builder) {
    Future<?> future = watchdogFactory.submitFunctionCall(timeoutInMilliseconds, builder
      .withResultConsumer(this)
      .build());
    while (!future.isDone()) {
      try {
        //noinspection BusyWait
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Assertions.fail(e);
      }
    }
    return getLastResult();
  }

  public Watchable<Object> createDecoratedRunnable(ExceptionRunnable runnable) {
    //noinspection unchecked
    return Watchable.builder(runnable)
      .withResultConsumer((ResultConsumer<Object>) this)
      .build();
  }

  public Watchable<OUT> createDecoratedCallable(Callable<OUT> callable) {
    return Watchable.builder(callable)
      .withResultConsumer(this)
      .build();
  }

  public <IN> WatchableWithInput<IN, Object> createDecoratedConsumer(ExceptionConsumer<IN> consumer) {
    //noinspection unchecked
    return Watchable.builder(consumer)
      .withResultConsumer((ResultConsumer<Object>) this)
      .build();
  }

  public <IN> WatchableWithInput<IN, OUT> createDecoratedFunction(ExceptionFunction<IN, OUT> function) {
    return Watchable.builder(function)
      .withResultConsumer(this)
      .build();
  }

}
