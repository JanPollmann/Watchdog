package de.pollmann.watchdog.tester.app;

import de.pollmann.watchdog.tester.tester.Tester;
import de.pollmann.watchdog.tester.tester.ValidationException;

import java.util.ArrayList;
import java.util.List;

public abstract class TesterApp extends FastLoopApp {

  protected final List<Tester> testers = new ArrayList<>();

  public TesterApp(AppContext appContext) {
    super(appContext);
  }

  @Override
  public Integer loop() {
    try {
      return testAll();
    } catch (Exception e) {
      return -1;
    }
  }

  private Integer testAll() throws Exception {
    for (Tester tester : testers) {
      tester.startTest();
    }
    for (Tester tester : testers) {
      try {
        tester.finishTest();
      } catch (ValidationException validationException) {
        // no op
      }
    }
    return OK;
  }

}
