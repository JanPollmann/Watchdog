package de.pollmann.watchdog.util.statistics.metric;

import de.pollmann.watchdog.util.statistics.TimestampProvider;

public class AverageTime extends AverageTimerPerFrame {

  public AverageTime(int historySize) {
    super(historySize);
  }

  @Override
  public long calculateTime(TimestampProvider memento) {
    return calculateDifference(memento.getStart(), memento.getEnd());
  }

}
