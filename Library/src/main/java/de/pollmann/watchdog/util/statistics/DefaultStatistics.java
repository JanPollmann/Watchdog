package de.pollmann.watchdog.util.statistics;

import de.pollmann.watchdog.util.statistics.metric.*;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class DefaultStatistics implements StatisticsIntern {

  private static final int HISTORY_SIZE = 10;
  private static final double FRAME_SIZE = 1000d;
  private static final int CALLS_PER_SECOND_INDEX = 0;
  private static final int AVERAGE_TIME_INDEX = 1;
  private static final int AVERAGE_CALL_TIME_INDEX = 2;
  private static final int AVERAGE_RESULT_CONSUMING_INDEX = 3;

  private final Metric[] metrics;
  private final AtomicInteger finishedInCurrentFrame = new AtomicInteger(0);

  private volatile long currentFrame = 0;
  private volatile int historyIndex = 0;

  public DefaultStatistics() {
    metrics = new Metric[4];
    metrics[CALLS_PER_SECOND_INDEX] = new CallsPerFrame(HISTORY_SIZE);
    metrics[AVERAGE_TIME_INDEX] = new AverageTime(HISTORY_SIZE);
    metrics[AVERAGE_CALL_TIME_INDEX] = new AverageCallTime(HISTORY_SIZE);
    metrics[AVERAGE_RESULT_CONSUMING_INDEX] = new AverageResultConsumingTime(HISTORY_SIZE);
  }

  @Override
  public TimestampProvider.TimestampSetter initialize() {
    return new Memento();
  }

  @Override
  public void finished(TimestampProvider.TimestampSetter state) {
    state.finished();
    updateTimeframe();
    finishedInCurrentFrame.incrementAndGet();
    for (int i = 0; i < metrics.length; i++) {
      metrics[i].mementoFinished(state);
    }
  }

  @Override
  public double getCallsPerSecond() {
    return getMetric(CALLS_PER_SECOND_INDEX);
  }

  @Override
  public double getAverageApproximatedCallTime() {
    return getMetric(AVERAGE_CALL_TIME_INDEX);
  }

  @Override
  public double getAverageApproximatedResultConsumingTime() {
    return getMetric(AVERAGE_RESULT_CONSUMING_INDEX);
  }

  @Override
  public double getAverageTime() {
    return getMetric(AVERAGE_TIME_INDEX);
  }

  private double getMetric(int index) {
    updateTimeframe();
    return metrics[index].get();
  }

  private void updateTimeframe() {
    long frame = calculateTimeFrame();
    if (frame != currentFrame) {
      synchronized (this) {
        frame = calculateTimeFrame();
        if (frame != currentFrame) {
          final int finishedInCurrentFrame = this.finishedInCurrentFrame.getAndSet(0);
          for (int i = 0; i < metrics.length; i++) {
            metrics[i].frameFinished(historyIndex, finishedInCurrentFrame);
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
