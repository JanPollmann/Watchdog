package de.pollmann.watchdog;

import de.pollmann.watchdog.exceptions.WatchableNotRepeatableException;
import de.pollmann.watchdog.tasks.Watchable;
import de.pollmann.watchdog.util.statistics.Memento;
import de.pollmann.watchdog.util.statistics.Statistics;

import java.util.Objects;
import java.util.concurrent.*;

class WatchdogWorker {

  private final ExecutorService workerPool;
  private final ExecutorService watchdogPool;

  public WatchdogWorker(ExecutorService watchdogPool, ExecutorService workerPool) {
    this.watchdogPool = Objects.requireNonNull(watchdogPool);
    this.workerPool = Objects.requireNonNull(workerPool);
  }

  public <OUT> Future<?> submitFunctionCall(long timeoutInMilliseconds, Watchable<OUT> watchable, Statistics statistics) {
    return watchdogPool.submit(() ->
      waitForCompletion(timeoutInMilliseconds, watchable, statistics)
    );
  }

  public <OUT> TaskResult<OUT> waitForCompletion(long timeoutInMilliseconds, Watchable<OUT> watchable, Statistics statistics) throws InterruptedException {
    TaskResult<OUT> taskResult = callWatchable(timeoutInMilliseconds, watchable, statistics);
    // TODO: make the ResultConsumer a watchable & monitor the result consumer as well? => user defines the result timeout
    // TODO: an alternative might be: #submitFunctionCall calls #waitForCompletion as watchable => 2 times the same timeout
    // "taskFinished" is a user provided function. An infinite loop may stop the termination of this function call
    watchable.taskFinished(taskResult);
    return taskResult;
  }

  /**
   * 1. {@link Watchable#start()} (makes sure the watchable is executed exactly once) <br>
   * 2. {@link Watchable#call()} <br>
   * 3. {@link Watchable#stop()} (interrupt worker if required)
   *
   * @return the result of no exception
   * @throws InterruptedException in case of an interrupt
   */
  private <OUT> TaskResult<OUT> callWatchable(long timeoutInMilliseconds, Watchable<OUT> watchable, Statistics statistics) throws InterruptedException {
    TaskResult<OUT> taskResult = startWatchable(watchable);
    if (taskResult == null) {
      // this area can only be entered by one thread! see startWatchable => Watchable#start
      Memento memento = statistics.beginCall();
      try {
        OUT result = submitStartedWatchableAndWaitForResult(timeoutInMilliseconds, watchable);
        taskResult = TaskResult.createOK(watchable, result);
      } catch (TimeoutException timeoutException) {
        taskResult = TaskResult.createTimeout(watchable, timeoutException);
      } catch (Throwable throwable) {
        taskResult = TaskResult.createError(watchable, throwable);
      } finally {
        // watchable stop will interrupt the thread in case of unfinished completion
        watchable.stop();
        statistics.stopCall(memento);
      }
    }
    return taskResult;
  }

  /**
   * @return the result of no exception
   * @throws InterruptedException in case of an interrupt
   * @throws ExecutionException in case of an execution exception
   * @throws TimeoutException in case of a timeout
   */
  private <OUT> OUT submitStartedWatchableAndWaitForResult(long timeoutInMilliseconds, Watchable<OUT> watchable) throws InterruptedException, ExecutionException, TimeoutException {
    Future<OUT> future = workerPool.submit(watchable);
    OUT result;
    if (timeoutInMilliseconds != 0) {
      result = future.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
    } else {
      result = future.get();
    }
    return result;
  }

  private <OUT> TaskResult<OUT> startWatchable(Watchable<OUT> watchable) {
    try {
      watchable.start();
    } catch (WatchableNotRepeatableException watchableNotRepeatableException) {
      return TaskResult.createError(watchable, watchableNotRepeatableException);
    }
    return null;
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
