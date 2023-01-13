package de.pollmann.watchdog.util.statistics.metric;

import de.pollmann.watchdog.util.statistics.TimestampProvider;

import java.util.Arrays;

public abstract class Metric {

  protected final int historySize;

  protected Metric(final int historySize) {
    this.historySize = historySize;
  }

  protected double calculateAverageOf(long[] array) {
    return Arrays.stream(array).sum() / (double) historySize;
  }

  protected double calculateAverageOf(double[] array) {
    return Arrays.stream(array).sum() / (double) historySize;
  }

  public abstract void mementoFinished(TimestampProvider memento);

  public abstract void frameFinished(int historyIndex, int finishedInCurrentFrame);

  public abstract double get();

}
