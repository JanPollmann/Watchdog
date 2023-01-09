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
      watchable.finishedWithResult(waitForCompletion(timeoutInMilliseconds, watchable))
    );
  }

  public <OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Watchable<OUT> watchable) {
    try {
      Future<OUT> future = workerPool.submit(watchable);
      OUT result = future.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
      // TODO: check if future isDone()
      return TaskResult.createOK(result);
    } catch (TimeoutException timeoutException) {
      return TaskResult.createTimeout(timeoutException);
    } catch (Throwable throwable) {
      return TaskResult.createError(throwable);
    }
  }
}
