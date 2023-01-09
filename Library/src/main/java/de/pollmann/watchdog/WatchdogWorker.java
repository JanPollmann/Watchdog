package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

class WatchdogWorker {

  private final ExecutorService workerPool;
  private final ExecutorService watchdogPool;

  public WatchdogWorker(ExecutorService watchdogPool, ExecutorService workerPool) {
    this.watchdogPool = Objects.requireNonNull(watchdogPool);
    this.workerPool = Objects.requireNonNull(workerPool);
  }

  public <OUT> Future<?> submitFunctionCall(long timeoutInMilliseconds, Watchable<OUT> watchable) {
    return watchdogPool.submit(() ->
      waitForCompletion(timeoutInMilliseconds, watchable)
    );
  }

  public <OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Watchable<OUT> watchable) {
    TaskResult<OUT> taskResult;
    try {
      Future<OUT> future = workerPool.submit(watchable);
      OUT result = future.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
      // TODO: check if future isDone()
      taskResult = TaskResult.createOK(result);
    } catch (TimeoutException timeoutException) {
      taskResult = TaskResult.createTimeout(timeoutException);
    } catch (Throwable throwable) {
      taskResult = TaskResult.createError(throwable);
    }
    watchable.finishedWithResult(taskResult);
    return taskResult;
  }
}
