package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
      OUT result;
      if (timeoutInMilliseconds != 0) {
        result = future.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
      } else {
        result = future.get();
      }
      // !future.isDone() cannot happen!
      taskResult = TaskResult.createOK(result);
    } catch (TimeoutException timeoutException) {
      taskResult = TaskResult.createTimeout(timeoutException);
    } catch (Throwable throwable) {
      taskResult = TaskResult.createError(throwable);
    }
    watchable.taskFinished(taskResult);
    return taskResult;
  }

  public boolean isTerminated() {
    return isTerminated(workerPool) || isTerminated(watchdogPool);
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    watchdogPool.shutdown();
    workerPool.shutdown();
  }

  private static boolean isTerminated(ExecutorService executorService) {
    return executorService.isTerminated() || executorService.isShutdown();
  }

}
