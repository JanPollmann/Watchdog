package de.pollmann.watchdog.util.statistics;

public interface TimestampProvider {

  Long getStart();
  Long getEnd();
  Long getBeginCall();
  Long getStopCall();
  Long getBeginResultConsuming();
  Long getStopResultConsuming();

  interface TimestampSetter extends TimestampProvider {
    void beginCall();
    void stopCall();
    void beginResultConsuming();
    void stopResultConsuming();
    void finished();
  }

}
