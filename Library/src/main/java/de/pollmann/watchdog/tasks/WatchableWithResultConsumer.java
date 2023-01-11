package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.WatchableNotRepeatableException;

import java.util.concurrent.CountDownLatch;

abstract class WatchableWithResultConsumer<OUT> implements Watchable<OUT> {

  protected final ResultConsumer<OUT> resultConsumer;
  private final CountDownLatch stopped = new CountDownLatch(2);

  private volatile Thread callerThread = null;

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
      try {
        callerThread.interrupt();
      } catch (NullPointerException nullPointerException) {
        // in case of a cleared reference: nothing to do
      }
    }
    stopped.await();
  }

  @Override
  public final synchronized OUT call() throws Exception {
    if (stopped()) {
      throw new WatchableNotRepeatableException();
    }
    callerThread = Thread.currentThread();
    stopped.countDown();
    try {
      return wrappedCall();
    } finally {
      stopped.countDown();
      callerThread = null;
    }
  }

  @Override
  public final boolean stopped() {
    return stopped.getCount() == 0;
  }

  protected abstract OUT wrappedCall() throws Exception;


}
