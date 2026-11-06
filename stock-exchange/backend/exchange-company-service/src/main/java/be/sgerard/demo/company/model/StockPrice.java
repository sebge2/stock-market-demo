package be.sgerard.demo.company.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * Price measure (OHLC) of a listing over a time interval.
 *
 */
@Entity
@Table(name = "stock_price", uniqueConstraints = @UniqueConstraint(name = "uk_stock_price_listing_ts_interval", columnNames = {"listing_id", "measured_at", "price_interval"}))
public class StockPrice {

  /**
   *
   * Internal unique identifier.
   *
   */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  /**
   *
   * Concerned listing
   *
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "listing_id", nullable = false, updatable = false)
  private Listing listing;

  /**
   *
   * Date and time of the measure (start of the interval)
   *
   */
  @NotNull
  @Column(name = "measured_at", nullable = false)
  private LocalDateTime timestamp;

  /**
   *
   * Opening price
   *
   */
  @NotNull
  @PositiveOrZero
  @Column(name = "open_price", nullable = false, precision = 19, scale = 4)
  private BigDecimal open;

  /**
   *
   * Highest price
   *
   */
  @NotNull
  @PositiveOrZero
  @Column(name = "high_price", nullable = false, precision = 19, scale = 4)
  private BigDecimal high;

  /**
   *
   * Lowest price
   *
   */
  @NotNull
  @PositiveOrZero
  @Column(name = "low_price", nullable = false, precision = 19, scale = 4)
  private BigDecimal low;

  /**
   *
   * Closing price
   *
   */
  @NotNull
  @PositiveOrZero
  @Column(name = "close_price", nullable = false, precision = 19, scale = 4)
  private BigDecimal close;

  /**
   *
   * Quantity of shares traded
   *
   */
  @PositiveOrZero
  @Column(name = "volume", nullable = false)
  private long volume;

  /**
   *
   * Time interval of the measure
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "price_interval", nullable = false, length = 10)
  private PriceInterval interval;

  /**
   *
   * Constructor required by JPA.
   *
   */
  protected StockPrice() {
  }

  /**
   *
   * Creates a new instance.
   *
   * @param listing Concerned listing
   * @param timestamp Date and time of the measure (start of the interval)
   * @param open Opening price
   * @param high Highest price
   * @param low Lowest price
   * @param close Closing price
   * @param volume Quantity of shares traded
   * @param interval Time interval of the measure
   *
   */
  public StockPrice(Listing listing, LocalDateTime timestamp, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, long volume, PriceInterval interval) {
    this.listing = listing;
    this.timestamp = timestamp;
    this.open = open;
    this.high = high;
    this.low = low;
    this.close = close;
    this.volume = volume;
    this.interval = interval;
  }

  /**
   *
   * @return the identifier
   *
   */
  public UUID getId() {
    return id;
  }

  /**
   *
   * @return the concerned listing
   *
   */
  public Listing getListing() {
    return listing;
  }

  /**
   *
   * @return the date and time of the measure (start of the interval)
   *
   */
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  /**
   *
   * @param timestamp the date and time of the measure (start of the interval)
   *
   */
  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  /**
   *
   * @return the opening price
   *
   */
  public BigDecimal getOpen() {
    return open;
  }

  /**
   *
   * @param open the opening price
   *
   */
  public void setOpen(BigDecimal open) {
    this.open = open;
  }

  /**
   *
   * @return the highest price
   *
   */
  public BigDecimal getHigh() {
    return high;
  }

  /**
   *
   * @param high the highest price
   *
   */
  public void setHigh(BigDecimal high) {
    this.high = high;
  }

  /**
   *
   * @return the lowest price
   *
   */
  public BigDecimal getLow() {
    return low;
  }

  /**
   *
   * @param low the lowest price
   *
   */
  public void setLow(BigDecimal low) {
    this.low = low;
  }

  /**
   *
   * @return the closing price
   *
   */
  public BigDecimal getClose() {
    return close;
  }

  /**
   *
   * @param close the closing price
   *
   */
  public void setClose(BigDecimal close) {
    this.close = close;
  }

  /**
   *
   * @return the quantity of shares traded
   *
   */
  public long getVolume() {
    return volume;
  }

  /**
   *
   * @param volume the quantity of shares traded
   *
   */
  public void setVolume(long volume) {
    this.volume = volume;
  }

  /**
   *
   * @return the time interval of the measure
   *
   */
  public PriceInterval getInterval() {
    return interval;
  }

  /**
   *
   * @param interval the time interval of the measure
   *
   */
  public void setInterval(PriceInterval interval) {
    this.interval = interval;
  }

  /**
   *
   * Entities are equal when they share the same non-null identifier.
   *
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StockPrice other)) {
      return false;
    }
    return id != null && Objects.equals(id, other.id);
  }

  /**
   *
   * Constant hash code, stable across the entity lifecycle.
   *
   */
  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  /**
   *
   * Textual representation without lazy associations.
   *
   */
  @Override
  public String toString() {
    return "StockPrice{id=" + id + ", timestamp=" + timestamp + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", volume=" + volume + ", interval=" + interval + "}";
  }
}
