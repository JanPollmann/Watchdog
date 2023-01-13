package de.pollmann.watchdog.util.statistics.metric;

import de.pollmann.watchdog.util.statistics.TimestampProvider;

public class AverageResultConsumingTime extends AverageTimerPerFrame {

  public AverageResultConsumingTime(int historySize) {
    super(historySize);
  }

  @Override
  public long calculateTime(TimestampProvider memento) {
    if (memento.getStopResultConsuming() == null) {
      return calculateDifference(memento.getBeginResultConsuming(), memento.getEnd());
    } else {
      return calculateDifference(memento.getBeginResultConsuming(), memento.getStopResultConsuming());
    }
  }

}
