package de.pollmann.watchdog;

public class WatchableOptions {
  private final long timeoutInMilliseconds;
  private final boolean monitoringEnabled;

  private WatchableOptions(Builder builder) {
    timeoutInMilliseconds = builder.timeoutInMilliseconds;
    monitoringEnabled = builder.monitoringEnabled;
  }

  public long getTimeoutInMilliseconds() {
    return timeoutInMilliseconds;
  }

  public boolean isMonitoringEnabled() {
    return monitoringEnabled;
  }

  public static Builder builder(long timeoutInMilliseconds) {
    return new Builder(timeoutInMilliseconds);
  }

  public static class Builder {
    private final long timeoutInMilliseconds;
    private boolean monitoringEnabled = false;

    public Builder(long timeoutInMilliseconds) {
      this.timeoutInMilliseconds = timeoutInMilliseconds;
    }

    /**
     * Enable statistics
     *
     * @return the builder for chaining
     */
    public Builder enableStatistics() {
      monitoringEnabled = true;
      return this;
    }

    /**
     * Disable statistics (default)
     *
     * @return the builder for chaining
     */
    public Builder disableStatistics() {
      monitoringEnabled = false;
      return this;
    }

    public WatchableOptions build() {
      return new WatchableOptions(this);
    }

  }

}
