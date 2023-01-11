package de.pollmann.watchdog.util.statistics;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultStatistics implements Statistics {

  private static final int HISTORY_SIZE = 10;
  private static final double FRAME_SIZE = 1000d;

  private final long[] averagePerFrame = new long[HISTORY_SIZE];
  private final AtomicLong finishedInCurrentFrame = new AtomicLong(0);

  private volatile long currentFrame = 0;
  private volatile int historyIndex = 0;
  private volatile double callsPerSecond = 0d;

  public DefaultStatistics() {

  }

  @Override
  public Memento beginCall() {
    return new Memento();
  }

  @Override
  public void stopCall(Memento state) {
    updateTimeframe();
    finishedInCurrentFrame.incrementAndGet();
  }

  private void updateTimeframe() {
    long frame = calculateTimeFrame();
    if (frame != currentFrame) {
      synchronized (this) {
        frame = calculateTimeFrame();
        if (frame != currentFrame) {
          // add data
          averagePerFrame[historyIndex] = finishedInCurrentFrame.get();
          // create statistics
          callsPerSecond = calculateAverageOf(averagePerFrame);
          // start a new frame
          historyIndex = (historyIndex + 1) % HISTORY_SIZE;
          currentFrame = frame;
          finishedInCurrentFrame.set(0);
        }
      }
    }
  }

  @Override
  public double getCallsPerSecond() {
    updateTimeframe();
    return callsPerSecond;
  }

  private long calculateTimeFrame() {
    return (long) (System.currentTimeMillis() / FRAME_SIZE);
  }

  private static double calculateAverageOf(long[] array) {
    return Arrays.stream(array).sum() / (double) HISTORY_SIZE;
  }

}
