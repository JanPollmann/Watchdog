package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.exceptions.WatchableInIncorrectState;
import de.pollmann.watchdog.exceptions.WatchableNotRepeatableException;

import java.util.concurrent.CountDownLatch;

abstract class WatchableWithResultConsumer<OUT> implements Watchable<OUT> {

  private static final int INITIAL_COUNT = 2;

  protected final ResultConsumer<OUT> resultConsumer;
  private final CountDownLatch stopped = new CountDownLatch(INITIAL_COUNT);

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
  public void stop() throws InterruptedException  {
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
  public final OUT call() throws Exception {
    if (stopped.getCount() == 1) {
      callerThread = Thread.currentThread();
      try {
        return wrappedCall();
      } finally {
        callerThread = null;
        stopped.countDown();
      }
    } else {
      throw new WatchableInIncorrectState(this);
    }
  }

  @Override
  public final boolean stopped() {
    return stopped.getCount() == 0;
  }

  @Override
  public void start() throws WatchableNotRepeatableException {
    if (stopped.getCount() == INITIAL_COUNT) {
      synchronized (this) {
        if (stopped.getCount() == INITIAL_COUNT) {
          stopped.countDown();
          return;
        }
      }
    }
    throw new WatchableNotRepeatableException(this);
  }

  protected abstract OUT wrappedCall() throws Exception;


}
