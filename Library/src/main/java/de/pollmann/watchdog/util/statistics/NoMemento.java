package de.pollmann.watchdog.util.statistics;

public class NoMemento implements TimestampProvider.TimestampSetter {
  @Override
  public Long getStart() {
    return null;
  }

  @Override
  public Long getEnd() {
    return null;
  }

  @Override
  public Long getBeginCall() {
    return null;
  }

  @Override
  public Long getStopCall() {
    return null;
  }

  @Override
  public Long getBeginResultConsuming() {
    return null;
  }

  @Override
  public Long getStopResultConsuming() {
    return null;
  }

  @Override
  public void beginCall() {

  }

  @Override
  public void stopCall() {

  }

  @Override
  public void beginResultConsuming() {

  }

  @Override
  public void stopResultConsuming() {

  }

  @Override
  public void finished() {

  }
}
