package de.pollmann.watchdog.util.statistics;

public class Memento {

  private final long start;

  private Long end;
  private Long beginCall;
  private Long stopCall;
  private Long beginResultConsuming;
  private Long stopResultConsuming;

  public Memento() {
    start = System.currentTimeMillis();
  }

  void beginCall() {
    beginCall = System.currentTimeMillis();
  }

  void stopCall() {
    stopCall = System.currentTimeMillis();
  }

  void beginResultConsuming() {
    beginResultConsuming = System.currentTimeMillis();
  }

  void stopResultConsuming() {
    stopResultConsuming = System.currentTimeMillis();
  }

  void finished() {
    end = System.currentTimeMillis();
  }

}
