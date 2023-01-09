package de.pollmann.watchdog.tester.app;

import de.pollmann.watchdog.tester.tester.RunnableOkTester;

public class RunnableTesterApp extends TesterApp {

  public RunnableTesterApp(AppContext appContext) {
    super(appContext);
    testers.add(new RunnableOkTester(context.getWatchdogFactory()));
  }

}
