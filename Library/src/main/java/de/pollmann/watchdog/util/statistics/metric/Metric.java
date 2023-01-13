package de.pollmann.watchdog.util.statistics.metric;

import de.pollmann.watchdog.util.statistics.Memento;

import java.util.Arrays;

public abstract class Metric {

  protected final int historySize;

  protected Metric(final int historySize) {
    this.historySize = historySize;
  }

  protected double calculateAverageOf(long[] array) {
    return Arrays.stream(array).sum() / (double) historySize;
  }

  public abstract void mementoFinished(Memento memento);

  public abstract void frameFinished(int historyIndex);

  public abstract double get();

}
