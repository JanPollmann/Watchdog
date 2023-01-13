package de.pollmann.watchdog.util.statistics.metric;

import de.pollmann.watchdog.util.statistics.TimestampProvider;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AverageTimerPerFrame extends Metric {

  private final double[] averageTimePerFrame;
  private final AtomicLong sumOfTimeInFrame = new AtomicLong(0);

  private volatile double averageOfAverageTimePerFrame;

  public AverageTimerPerFrame(final int historySize) {
    super(historySize);
    averageTimePerFrame = new double[historySize];
  }

  @Override
  public void frameFinished(int historyIndex, int finishedInCurrentFrame) {
    if (finishedInCurrentFrame > 0) {
      averageTimePerFrame[historyIndex] = sumOfTimeInFrame.getAndSet(0) / (double) finishedInCurrentFrame;
    } else {
      averageTimePerFrame[historyIndex] = 0;
    }
    averageOfAverageTimePerFrame = calculateAverageOf(averageTimePerFrame);
  }

  @Override
  public void mementoFinished(TimestampProvider memento) {
    sumOfTimeInFrame.addAndGet(calculateTime(memento));
  }

  @Override
  public double get() {
    return averageOfAverageTimePerFrame;
  }

  protected long calculateDifference(Long start, Long end) {
    if (start != null && end != null) {
      return end - start;
    } else {
      return 0;
    }
  }

  public abstract long calculateTime(TimestampProvider memento);

}
