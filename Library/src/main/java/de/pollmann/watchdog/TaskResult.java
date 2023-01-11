package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;

import java.util.concurrent.ExecutionException;

public class TaskResult<OUT> {

  private final ResultCode code;
  private final ExecutionException executionException;
  private final Throwable errorReason;
  private final OUT result;
  private final Watchable<OUT> watchable;

  private TaskResult(Watchable<OUT> watchable, ResultCode code, OUT result, Throwable errorReason, ExecutionException executionException) {
    this.watchable = watchable;
    this.code = code;
    this.result = result;
    this.errorReason = errorReason;
    this.executionException = executionException;
  }

  private TaskResult(Watchable<OUT> watchable, ExecutionException executionException) {
    this(watchable, ResultCode.ERROR, null, executionException.getCause(), executionException);
  }

  private TaskResult(Watchable<OUT> watchable, Throwable errorReason) {
    this(watchable, ResultCode.ERROR, null, errorReason, null);
  }

  static <OUT> TaskResult<OUT> createOK(Watchable<OUT> watchable, OUT result) {
    return new TaskResult<>(watchable, ResultCode.OK, result, null, null);
  }

  static <OUT> TaskResult<OUT> createTimeout(Watchable<OUT> watchable, Throwable exception) {
    return new TaskResult<>(watchable, ResultCode.TIMEOUT, null, exception, null);
  }

  static <OUT> TaskResult<OUT> createError(Watchable<OUT> watchable, Throwable throwable) {
    if (throwable instanceof ExecutionException) {
      return new TaskResult<>(watchable, (ExecutionException) throwable);
    } else {
      return new TaskResult<>(watchable, throwable);
    }
  }

  public ResultCode getCode() {
    return code;
  }

  public Throwable getErrorReason() {
    return errorReason;
  }

  public ExecutionException getExecutionException() {
    return executionException;
  }

  public OUT getResult() {
    return result;
  }

  public boolean hasError() {
    return code != ResultCode.OK;
  }
  
  public Watchable<OUT> getWatchable() {
    return watchable;
  }

}
