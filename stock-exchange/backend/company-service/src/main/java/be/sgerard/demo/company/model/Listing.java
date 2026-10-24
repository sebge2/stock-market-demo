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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * The listing of a security on an exchange; a security may be listed on several markets.
 *
 */
@Entity
@Table(name = "listing", uniqueConstraints = @UniqueConstraint(name = "uk_listing_exchange_ticker", columnNames = {"exchange_id", "ticker"}))
public class Listing {

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
   * Listed security
   *
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "security_id", nullable = false, updatable = false)
  private Security security;

  /**
   *
   * Exchange on which the security is listed
   *
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "exchange_id", nullable = false, updatable = false)
  private Exchange exchange;

  /**
   *
   * Ticker symbol used on this market
   *
   */
  @NotBlank
  @Size(max = 20)
  @Column(name = "ticker", nullable = false, length = 20)
  private String ticker;

  /**
   *
   * Trading currency (ISO 4217)
   *
   */
  @NotBlank
  @Pattern(regexp = "[A-Z]{3}")
  @Column(name = "trading_currency", nullable = false, length = 3)
  private String tradingCurrency;

  /**
   *
   * Date of first listing
   *
   */
  @NotNull
  @Column(name = "listing_date", nullable = false)
  private LocalDate listingDate;

  /**
   *
   * Date of delisting, if any
   *
   */
  @Column(name = "delisting_date")
  private LocalDate delistingDate;

  /**
   *
   * Status of the listing
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private ListingStatus status;

  /**
   *
   * Whether this is the primary listing of the security
   *
   */
  @Column(name = "is_primary", nullable = false)
  private boolean primary;

  /**
   *
   * Constructor required by JPA.
   *
   */
  protected Listing() {
  }

  /**
   *
   * Creates a new instance.
   *
   * @param security Listed security
   * @param exchange Exchange on which the security is listed
   * @param ticker Ticker symbol used on this market
   * @param tradingCurrency Trading currency (ISO 4217)
   * @param listingDate Date of first listing
   * @param status Status of the listing
   * @param primary Whether this is the primary listing of the security
   *
   */
  public Listing(Security security, Exchange exchange, String ticker, String tradingCurrency, LocalDate listingDate, ListingStatus status, boolean primary) {
    this.security = security;
    this.exchange = exchange;
    this.ticker = ticker;
    this.tradingCurrency = tradingCurrency;
    this.listingDate = listingDate;
    this.status = status;
    this.primary = primary;
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
   * @return the listed security
   *
   */
  public Security getSecurity() {
    return security;
  }

  /**
   *
   * @return the exchange on which the security is listed
   *
   */
  public Exchange getExchange() {
    return exchange;
  }

  /**
   *
   * @return the ticker symbol used on this market
   *
   */
  public String getTicker() {
    return ticker;
  }

  /**
   *
   * @param ticker the ticker symbol used on this market
   *
   */
  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  /**
   *
   * @return the trading currency (ISO 4217)
   *
   */
  public String getTradingCurrency() {
    return tradingCurrency;
  }

  /**
   *
   * @param tradingCurrency the trading currency (ISO 4217)
   *
   */
  public void setTradingCurrency(String tradingCurrency) {
    this.tradingCurrency = tradingCurrency;
  }

  /**
   *
   * @return the date of first listing
   *
   */
  public LocalDate getListingDate() {
    return listingDate;
  }

  /**
   *
   * @param listingDate the date of first listing
   *
   */
  public void setListingDate(LocalDate listingDate) {
    this.listingDate = listingDate;
  }

  /**
   *
   * @return the date of delisting, if any
   *
   */
  public LocalDate getDelistingDate() {
    return delistingDate;
  }

  /**
   *
   * @param delistingDate the date of delisting, if any
   *
   */
  public void setDelistingDate(LocalDate delistingDate) {
    this.delistingDate = delistingDate;
  }

  /**
   *
   * @return the status of the listing
   *
   */
  public ListingStatus getStatus() {
    return status;
  }

  /**
   *
   * @param status the status of the listing
   *
   */
  public void setStatus(ListingStatus status) {
    this.status = status;
  }

  /**
   *
   * @return whether this is the primary listing of the security
   *
   */
  public boolean isPrimary() {
    return primary;
  }

  /**
   *
   * @param primary whether this is the primary listing of the security
   *
   */
  public void setPrimary(boolean primary) {
    this.primary = primary;
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
    if (!(o instanceof Listing other)) {
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
    return "Listing{id=" + id + ", ticker=" + ticker + ", tradingCurrency=" + tradingCurrency + ", listingDate=" + listingDate + ", status=" + status + ", primary=" + primary + "}";
  }
}
