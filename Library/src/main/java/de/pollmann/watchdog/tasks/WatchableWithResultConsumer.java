package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

abstract class WatchableWithResultConsumer<OUT> implements Watchable<OUT> {

  protected final ResultConsumer<OUT> resultConsumer;

  private volatile Thread callerThread = null;
  private volatile boolean stopped;

  protected WatchableWithResultConsumer(WatchableBuilder<?, OUT, ?, ?> builder) {
    if (builder.resultConsumer == null) {
      this.resultConsumer = WatchableWithResultConsumer::emptyConsumer;
    } else {
      this.resultConsumer = builder.resultConsumer;
    }
  }

  @Override
  public final void taskFinished(TaskResult<OUT> taskResult) {
    resultConsumer.accept(taskResult);
  }

  private static void emptyConsumer(TaskResult<?> taskResult) {

  }

  @Override
  public void stop() throws InterruptedException {
    if (callerThread != null) {
      callerThread.interrupt();
      long sleepDuration = 1;
      while (!stopped) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        //noinspection BusyWait
        Thread.sleep(sleepDuration);
        sleepDuration = Math.max(sleepDuration + 1, 1000);
      }
    }
  }

  @Override
  public final OUT call() throws Exception {
    callerThread = Thread.currentThread();
    try {
      return wrappedCall();
    } finally {
      stopped = true;
      callerThread = null;
    }
  }

  @Override
  public final boolean stopped() {
    return stopped;
  }

  protected abstract OUT wrappedCall() throws Exception;


}
