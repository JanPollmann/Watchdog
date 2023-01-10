package de.pollmann.watchdog.tasks;

public abstract class WatchableBuilder<IN, OUT, TaskType, WatchableType extends Watchable<OUT>> {

  protected ResultConsumer<OUT> resultConsumer;
  protected TaskType task;
  protected IN input;

  protected WatchableBuilder(TaskType task) {
    withTask(task);
  }

  public WatchableBuilder<IN, OUT, TaskType, WatchableType> withTask(TaskType task) {
    this.task = task;
    return this;
  }

  public WatchableBuilder<IN, OUT, TaskType, WatchableType> withResultConsumer(ResultConsumer<OUT> resultConsumer) {
    this.resultConsumer = resultConsumer;
    return this;
  }

  public WatchableBuilder<IN, OUT, TaskType, WatchableType> withInput(IN input) {
    this.input = input;
    return this;
  }

  public abstract WatchableType build();

}


