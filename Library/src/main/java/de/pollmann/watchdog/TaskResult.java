package de.pollmann.watchdog;

import de.pollmann.watchdog.tasks.Watchable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class TaskResult<OUT> {

  private final ResultCode code;
  private final ExecutionException executionException;
  private final Throwable errorReason;
  private final OUT result;
  private final Watchable<OUT> watchable;

  private TaskResult(Watchable<OUT> watchable, ResultCode code, Throwable throwable, OUT result) {
    this.watchable = watchable;
    this.code = code;
    this.result = result;
    if (throwable instanceof ExecutionException) {
      executionException = (ExecutionException) throwable;
      errorReason = executionException.getCause();
    } else {
      executionException = null;
      errorReason = throwable;
    }
  }

  static <OUT> TaskResult<OUT> createOK(Watchable<OUT> watchable, OUT result) {
    return new TaskResult<>(watchable, ResultCode.OK, null, result);
  }

  static <OUT> TaskResult<OUT> createTimeout(Watchable<OUT> watchable, TimeoutException exception) {
    return new TaskResult<>(watchable, ResultCode.TIMEOUT, exception, null);
  }

  static <OUT> TaskResult<OUT> createError(Watchable<OUT> watchable, Throwable throwable) {
    return new TaskResult<>(watchable, ResultCode.ERROR, throwable, null);
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
