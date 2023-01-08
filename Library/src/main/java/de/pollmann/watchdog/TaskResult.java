package de.pollmann.watchdog;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class TaskResult<T> {

  private final ResultCode code;
  private final ExecutionException executionException;
  private final Throwable errorReason;
  private final T result;

  private TaskResult(ResultCode code, Throwable throwable, T result) {
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

  static <T> TaskResult<T> createOK(T result) {
    return new TaskResult<>(ResultCode.OK, null, result);
  }

  static <T> TaskResult<T> createTimeout(TimeoutException exception) {
    return new TaskResult<>(ResultCode.TIMEOUT, exception, null);
  }

  static <T> TaskResult<T> createError(Throwable throwable) {
    return new TaskResult<>(ResultCode.ERROR, throwable, null);
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

  public T getResult() {
    return result;
  }

  public boolean hasError() {
    return code != ResultCode.OK;
  }
}
