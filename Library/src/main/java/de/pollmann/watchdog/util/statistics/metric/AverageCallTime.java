package de.pollmann.watchdog.util.statistics.metric;

import de.pollmann.watchdog.util.statistics.TimestampProvider;

public class AverageCallTime extends AverageTimerPerFrame {

  public AverageCallTime(int historySize) {
    super(historySize);
  }

  @Override
  public long calculateTime(TimestampProvider memento) {
    if (memento.getStopCall() == null) {
      return calculateDifference(memento.getBeginCall(), memento.getEnd());
    } else {
      return calculateDifference(memento.getBeginCall(), memento.getStopCall());
    }
  }

}
