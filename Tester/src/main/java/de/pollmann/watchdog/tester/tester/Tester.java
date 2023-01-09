package de.pollmann.watchdog.tester.tester;

public interface Tester {
  void startTest() throws Exception;
  void finishTest() throws Exception;
}
