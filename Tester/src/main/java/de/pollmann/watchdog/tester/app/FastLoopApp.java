package de.pollmann.watchdog.tester.app;

import de.pollmann.watchdog.RepeatableTaskWithoutInput;
import de.pollmann.watchdog.ResultCode;
import de.pollmann.watchdog.TaskResult;
import de.pollmann.watchdog.WatchdogFactory;
import de.pollmann.watchdog.tasks.Watchable;

import java.util.Objects;

public abstract class FastLoopApp {

  protected static final Integer OK = 0;

  protected final AppContext context;

  private final WatchdogFactory coreFactory;
  /**
   * create the main loop callable. The result is the exit code. The main loop continues as long as the exit code is {@value OK} and the result code is {@link ResultCode#OK}
   */
  private final RepeatableTaskWithoutInput<Integer> mainLoop;

  public FastLoopApp(AppContext appContext) {
    context = appContext;
    coreFactory = new WatchdogFactory("core");
    // create the main loop callable with enabled statistics
    mainLoop = coreFactory.createRepeated(context.getLoopTimeout(), true, Watchable.builder(this::loop)
      // register a loop finished listener
      .withResultConsumer(this::onLoopFinished)
      .build()
    );
  }

  /**
   * Start the application
   */
  public final void start() {
    // timeout 0: no timeout, block as long as the task takes
    coreFactory.waitForCompletion(0,
        Watchable.builder(() -> {
          TaskResult<Integer> result;
          boolean stop = false;
          double lastLoopsPerSecond = 0;
          // loop as fast as possible
          do {
            if (Thread.interrupted()) {
              throw new InterruptedException();
            }
            // call the main loop once
            result = mainLoop.waitForCompletion();
            // print statistics (statistics are enabled for the Repeated Task "mainLoop"!)
            if (lastLoopsPerSecond != mainLoop.getCallsPerSecond()) {
              lastLoopsPerSecond = mainLoop.getCallsPerSecond();
              System.out.printf("Current loops per second: %.2f%n", lastLoopsPerSecond);
            }
            // The main loop continues as long as this condition is true
            if (result.getCode() != ResultCode.OK || !Objects.equals(result.getResult(), OK)) {
              stop = true;
            }
          } while (!stop);
          return result.getResult();
        })
        // register an exit listener
        .withResultConsumer(this::onExit)
        .build()
    );
  }

  /**
   * The main loop continues as long as the exit code is {@value OK} and no timeout ({@link ResultCode#TIMEOUT}) or error ({@link ResultCode#ERROR}) occurred
   *
   * @return the exit code
   */
  public abstract Integer loop() throws Exception;

  /**
   * The main loop result containing the last exit code of {@link #loop()}
   *
   * @param taskResult the result of the main loop (NOT the taskResult of {@link #loop()}!)
   */
  public void onExit(TaskResult<Integer> taskResult) {

  }

  /**
   * Listener for each execution of {@link #loop()}
   *
   * @param taskResult the taskResult of {@link #loop()}
   */
  public void onLoopFinished(TaskResult<Integer> taskResult) {

  }

}
