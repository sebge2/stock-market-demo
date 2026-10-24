package be.sgerard.demo.company.model;

/**
 *
 * Time interval covered by a {@link StockPrice} measure.
 *
 */
public enum PriceInterval {

  /**
   *
   * One minute.
   *
   */
  ONE_MINUTE("1m"),

  /**
   *
   * Five minutes.
   *
   */
  FIVE_MINUTES("5m"),

  /**
   *
   * One hour.
   *
   */
  ONE_HOUR("1h"),

  /**
   *
   * One day.
   *
   */
  ONE_DAY("1d");

  /**
   *
   * Short label of the interval (for instance 1m).
   *
   */
  private final String label;

  /**
   *
   * Creates an interval.
   *
   * @param label short label of the interval
   */
  PriceInterval(String label) {
    this.label = label;
  }

  /**
   *
   * Returns the short label of the interval.
   *
   * @return the label
   */
  public String getLabel() {
    return label;
  }
}
