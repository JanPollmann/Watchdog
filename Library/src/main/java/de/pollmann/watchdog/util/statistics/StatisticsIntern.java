package de.pollmann.watchdog.util.statistics;

public interface StatisticsIntern extends Statistics {
  /**
   * The library calls this function as soon as the function call starts
   *
   * @return the memento
   */
  Memento beginCall();

  /**
   * The library calls this function as soon as the function call ends
   *
   * @param state the memento returned by {@link #beginCall()}
   */
  void stopCall(Memento state);
}
