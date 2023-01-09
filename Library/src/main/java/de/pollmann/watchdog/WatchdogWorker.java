package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;

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
      if (future.isDone()){
        taskResult = TaskResult.createOK(result);
      } else {
        taskResult = unfinishedFuture(future);
      }
    } catch (TimeoutException timeoutException) {
      taskResult = TaskResult.createTimeout(timeoutException);
    } catch (Throwable throwable) {
      taskResult = TaskResult.createError(throwable);
    }
    Consumer<TaskResult<OUT>> consumer = watchable.getResultConsumer();
    if (consumer != null) {
      consumer.accept(taskResult);
    }
    return taskResult;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    watchdogPool.shutdown();
    workerPool.shutdown();
  }

  private <OUT> TaskResult<OUT> unfinishedFuture(Future<OUT> future) {
    if (!future.isCancelled()) {
      future.cancel(true);
    }
    return TaskResult.createError(new UnfinishedFutureException(future));
  }

}
