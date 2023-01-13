package de.pollmann.watchdog.util.statistics;

import de.pollmann.watchdog.util.statistics.metric.CallsPerFrame;
import de.pollmann.watchdog.util.statistics.metric.Metric;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class DefaultStatistics implements StatisticsIntern {

  private static final int HISTORY_SIZE = 10;
  private static final double FRAME_SIZE = 1000d;
  private static final int CALLS_PER_SECOND_INDEX = 0;

  private final Metric[] metrics;

  private volatile long currentFrame = 0;
  private volatile int historyIndex = 0;

  public DefaultStatistics() {
    metrics = new Metric[1];
    // calls per second
    metrics[CALLS_PER_SECOND_INDEX] = new CallsPerFrame(HISTORY_SIZE);
    //
  }

  @Override
  public Memento initialize() {
    return new Memento();
  }

  @Override
  public void beginCall(Memento state) {
    updateTimeframe();
    state.beginCall();
  }

  @Override
  public void stopCall(Memento state) {
    updateTimeframe();
    state.stopCall();
  }

  @Override
  public void beginResultConsuming(Memento state) {
    updateTimeframe();
    state.beginResultConsuming();
  }

  @Override
  public void stopResultConsuming(Memento state) {
    updateTimeframe();
    state.stopResultConsuming();
  }

  @Override
  public void finished(Memento state) {
    updateTimeframe();
    finishMemento(state);
  }

  private void finishMemento(Memento state) {
    updateTimeframe();
    state.finished();
    for (int i = 0; i < metrics.length; i++) {
      metrics[i].mementoFinished(state);
    }
  }

  @Override
  public double getCallsPerSecond() {
    updateTimeframe();
    return metrics[CALLS_PER_SECOND_INDEX].get();
  }

  private void updateTimeframe() {
    long frame = calculateTimeFrame();
    if (frame != currentFrame) {
      synchronized (this) {
        frame = calculateTimeFrame();
        if (frame != currentFrame) {
          for (int i = 0; i < metrics.length; i++) {
            metrics[i].frameFinished(historyIndex);
          }
          // start a new frame
          historyIndex = (historyIndex + 1) % HISTORY_SIZE;
          currentFrame = frame;
        }
      }
    }
  }

  private long calculateTimeFrame() {
    return (long) (System.currentTimeMillis() / FRAME_SIZE);
  }

}
