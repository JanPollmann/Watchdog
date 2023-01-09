package de.pollmann.watchdog.tester.app;

import de.pollmann.watchdog.RepeatableTaskWithoutInput;
import de.pollmann.watchdog.ResultCode;
import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.WatchdogFactory;

import java.util.Objects;

public abstract class FastLoopApp {

  protected static final Integer OK = 0;

  protected final AppContext context;

  private final WatchdogFactory coreFactory;
  private final RepeatableTaskWithoutInput<Integer> mainLoop;

  public FastLoopApp(AppContext appContext) {
    context = appContext;
    coreFactory = new WatchdogFactory("core");
    mainLoop = coreFactory.createRepeated(context.getLoopTimeout(), this::loop);
  }

  public final void start() {
    coreFactory.waitForCompletion(0, () -> {
      boolean stop = false;
      while (!stop) {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        TaskResult<Integer> result = mainLoop.waitForCompletion();
        if (result.getCode() != ResultCode.OK || !Objects.equals(result.getResult(), OK)) {
          stop = true;
        }
      }
    });
  }

  public abstract Integer loop();

}
