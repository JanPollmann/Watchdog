package de.pollmann.watchdog;

import java.util.Objects;

abstract class RepeatableTask {

  private WatchdogWorker worker;

  public RepeatableTask(WatchdogWorker worker) {
    this.worker = Objects.requireNonNull(worker);
  }

  public final void terminate() {
    this.worker = null;
  }

  public final boolean isTerminated() {
    if (worker == null) {
      return true;
    } else if (worker.isTerminated()){
      terminate();
      return true;
    }
    return false;
  }

  protected final WatchdogWorker getWorkerIfAvailable() {
    if (isTerminated()) {
      throw new RepeatableTaskTerminatedException(this);
    }
    return worker;
  }





}
