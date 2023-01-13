package de.pollmann.watchdog.util.statistics.metric;

import de.pollmann.watchdog.util.statistics.Memento;

import java.util.concurrent.atomic.AtomicLong;

public class CallsPerFrame extends Metric {

  private final long[] data;
  private final AtomicLong finishedInCurrentFrame = new AtomicLong(0);

  private volatile double callsPerSecond;

  public CallsPerFrame(final int historySize) {
    super(historySize);
    data = new long[historySize];
  }

  @Override
  public void frameFinished(int historyIndex) {
    data[historyIndex] = finishedInCurrentFrame.getAndSet(0);
    callsPerSecond = calculateAverageOf(data);
  }

  @Override
  public void mementoFinished(Memento memento) {
    finishedInCurrentFrame.incrementAndGet();
  }

  @Override
  public double get() {
    return callsPerSecond;
  }

}
