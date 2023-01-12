package de.pollmann.watchdog.tasks;

import de.pollmann.watchdog.exceptions.WatchableNotRepeatableException;

/**
 * Internal support functions for {@link Watchable}s
 *
 * BEWARE: {@link Watchable} are copied if required!
 * @see Repeatable
 */
interface Stoppable {

  /**
   * Internal support function. Interrupt the worker if required
   *
   * BEWARE: {@link Watchable}s are copied if required
   *
   * @throws InterruptedException if externally interrupted (use case: {@link Watchable} does not respond to an interrupt)
   */
  void stop() throws InterruptedException;

  /**
   * Internal support function. Signals: The watchable was called once and completed the execution
   *
   * BEWARE: {@link Watchable}s are copied if required
   *
   * @return stopped or not
   */
  boolean stopped();

  /**
   * Internal support function. Makes sure the watchable is executed exactly once
   *
   * BEWARE: {@link Watchable}s are copied if required
   *
   * @throws WatchableNotRepeatableException if the watchable was already started
   */
  void start() throws WatchableNotRepeatableException;

}
