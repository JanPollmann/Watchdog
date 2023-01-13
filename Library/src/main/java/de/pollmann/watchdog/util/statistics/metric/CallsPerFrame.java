package de.pollmann.watchdog.util.statistics.metric;

import de.pollmann.watchdog.util.statistics.TimestampProvider;

public class CallsPerFrame extends Metric {

  private final long[] data;

  private volatile double callsPerSecond;

  public CallsPerFrame(final int historySize) {
    super(historySize);
    data = new long[historySize];
  }

  @Override
  public void frameFinished(int historyIndex, int finishedInCurrentFrame) {
    data[historyIndex] = finishedInCurrentFrame;
    callsPerSecond = calculateAverageOf(data);
  }

  @Override
  public void mementoFinished(TimestampProvider memento) {

  }

  @Override
  public double get() {
    return callsPerSecond;
  }

}
