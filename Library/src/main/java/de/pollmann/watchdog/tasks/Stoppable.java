package de.pollmann.watchdog.tasks;

/**
 * Internal support functions for {@link Watchable}s
 *
 * BEWARE: {@link Watchable} are copied if required!
 * @see Repeatable
 */
interface Stoppable {

  /**
   * BEWARE: {@link Watchable} are copied if required => you might not do what you think to do
   *
   * @throws InterruptedException jf interrupted
   */
  void stop() throws InterruptedException;

  /**
   * BEWARE: {@link Watchable} are copied if required => you might not do what you think to do
   *
   * @return stopped or not
   */
  boolean stopped();

}
