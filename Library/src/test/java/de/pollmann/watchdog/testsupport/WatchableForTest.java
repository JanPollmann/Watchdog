package de.pollmann.watchdog.testsupport;

import de.pollmann.watchdog.TaskResult;

interface WatchableForTest<T> {
  TaskResult<T> getLastResult();
}
