package de.pollmann.watchdog.util.statistics;

public class Memento implements TimestampProvider.TimestampSetter {

  private final Long start;

  private Long end;
  private Long beginCall;
  private Long stopCall;
  private Long beginResultConsuming;
  private Long stopResultConsuming;

  public Memento() {
    start = System.nanoTime();
  }

  public void beginCall() {
    beginCall = System.nanoTime();
  }

  public void stopCall() {
    stopCall = System.nanoTime();
  }

  public void beginResultConsuming() {
    beginResultConsuming = System.nanoTime();
  }

  public void stopResultConsuming() {
    stopResultConsuming = System.nanoTime();
  }

  public void finished() {
    end = System.nanoTime();
  }

  public Long getStart() {
    return start;
  }

  public Long getEnd() {
    return end;
  }

  public Long getBeginCall() {
    return beginCall;
  }

  public Long getStopCall() {
    return stopCall;
  }

  public Long getBeginResultConsuming() {
    return beginResultConsuming;
  }

  public Long getStopResultConsuming() {
    return stopResultConsuming;
  }

}
