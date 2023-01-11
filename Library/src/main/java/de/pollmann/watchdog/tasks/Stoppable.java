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
   * Internal support function
   *
   * BEWARE: {@link Watchable} are copied if required
   *
   * @throws InterruptedException jf interrupted
   */
  void stop() throws InterruptedException;

  /**
   * Internal support function
   *
   * BEWARE: {@link Watchable} are copied if required
   *
   * @return stopped or not
   */
  boolean stopped();

  /**
   * Internal support function
   *
   * BEWARE: {@link Watchable} are copied if required
   * @throws WatchableNotRepeatableException if the watchable was already started
   */
  void start() throws WatchableNotRepeatableException;

}
