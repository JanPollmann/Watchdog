package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.TaskResult;

import java.util.function.Consumer;

public interface ResultConsumer<OUT> extends Consumer<TaskResult<OUT>> {
}
