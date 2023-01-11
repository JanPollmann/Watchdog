package de.pollmann.watchdog.tasks;

/**
 * Internal support functions for {@link Watchable}s
 */
public interface Repeatable<OUT> {
  /**
   * {@link Watchable} are copied if required
   */
  WatchableBuilder<?, OUT, ?, ? extends Watchable<OUT>> copy();
}
