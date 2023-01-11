package de.pollmann.watchdog.tasks;

public abstract class WatchableBuilder<IN, OUT, TaskType, WatchableType extends Watchable<OUT>> {

  protected ResultConsumer<OUT> resultConsumer;
  protected TaskType task;
  protected IN input;

  /**
   * Builder for a watchable
   *
   * @param task the submitted task
   */
  protected WatchableBuilder(TaskType task) {
    this.task = task;
  }

  /**
   * Optional callback for the task result e.g. for submitted task results.
   *
   * BEWARE: this listener is NOT monitored and could prevent the watchdog termination
   *
   * @param resultConsumer the consumer
   * @return the builder for chaining
   */
  public WatchableBuilder<IN, OUT, TaskType, WatchableType> withResultConsumer(ResultConsumer<OUT> resultConsumer) {
    this.resultConsumer = resultConsumer;
    return this;
  }

  /**
   * For tasks with an input, set the input here. Only required for direct invocation and not for repeated tasks
   *
   * @param input the input (if required)
   * @return the builder for chaining
   * @see de.pollmann.watchdog.RepeatableTaskWithInput#submitFunctionCall(Object) submitFunctionCall - for repeated tasks with input
   * @see de.pollmann.watchdog.RepeatableTaskWithInput#waitForCompletion(Object) waitForCompletion - for repeated tasks with input
   */
  public WatchableBuilder<IN, OUT, TaskType, WatchableType> withInput(IN input) {
    this.input = input;
    return this;
  }

  public abstract WatchableType build();

}


