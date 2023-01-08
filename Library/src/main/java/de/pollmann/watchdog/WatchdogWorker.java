package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.WatchableCallable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

class WatchdogWorker {

  private final ExecutorService workerPool;
  private final ExecutorService watchdogPool;

  public WatchdogWorker(ExecutorService watchdogPool, ExecutorService workerPool) {
    this.watchdogPool = watchdogPool;
    this.workerPool = workerPool;
  }

  public <T> Future<?> submitFunctionCall(long timeoutInMilliseconds, WatchableCallable<T> callable) {
    return watchdogPool.submit(() ->
      callable.finishedWithResult(waitForCompletion(timeoutInMilliseconds, callable))
    );
  }

  public <T> TaskResult<T> waitForCompletion(long timeoutInMilliseconds, Callable<T> callable) {
    try {
      Future<T> future = workerPool.submit(callable);
      T result = future.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
      return TaskResult.createOK(result);
    } catch (TimeoutException timeoutException) {
      return TaskResult.createTimeout(timeoutException);
    } catch (Throwable throwable) {
      return TaskResult.createError(throwable);
    }
  }
}
