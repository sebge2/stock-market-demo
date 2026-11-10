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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * A financial instrument issued by a company; a company may issue several securities.
 *
 */
@Entity
@Table(name = "security")
public class Security {

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
   * Issuing company
   *
   */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "company_id", nullable = false, updatable = false)
  private Company company;

  /**
   *
   * Name of the instrument
   *
   */
  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false)
  private String name;

  /**
   *
   * International securities identification number (ISIN)
   *
   */
  @NotBlank
  @Pattern(regexp = "[A-Z]{2}[A-Z0-9]{9}[0-9]")
  @Column(name = "isin", nullable = false, unique = true, length = 12)
  private String isin;

  /**
   *
   * Type of financial instrument
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private SecurityType type;

  /**
   *
   * Reference currency (ISO 4217)
   *
   */
  @NotBlank
  @Pattern(regexp = "[A-Z]{3}")
  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  /**
   *
   * Ticker symbol used on the single exchange.
   *
   */
  @NotBlank
  @Size(max = 20)
  @Column(name = "ticker", nullable = false, unique = true, length = 20)
  private String ticker;

  /**
   *
   * Currency used to trade the security on the exchange.
   *
   */
  @NotBlank
  @Pattern(regexp = "[A-Z]{3}")
  @Column(name = "trading_currency", nullable = false, length = 3)
  private String tradingCurrency;

  /**
   *
   * Date on which the security was admitted to trading.
   *
   */
  @NotNull
  @Column(name = "listing_date", nullable = false)
  private LocalDate listingDate;

  /**
   *
   * Date on which the security was delisted, when applicable.
   *
   */
  @Column(name = "delisting_date")
  private LocalDate delistingDate;

  /**
   *
   * Number of issued shares, if applicable
   *
   */
  @PositiveOrZero
  @Column(name = "issued_shares")
  private Long issuedShares;

  /**
   *
   * Number of outstanding shares, if applicable
   *
   */
  @PositiveOrZero
  @Column(name = "outstanding_shares")
  private Long outstandingShares;

  /**
   *
   * Trading status of the instrument on the single exchange.
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private SecurityStatus status;

  /**
   *
   * Most recently executed price on the exchange, in the trading currency.
   *
   */
  @PositiveOrZero
  @Column(name = "current_price", precision = 19, scale = 4)
  private BigDecimal currentPrice;

  /**
   *
   * Date and time at which the current price was last updated.
   *
   */
  @Column(name = "current_price_updated_at")
  private LocalDateTime currentPriceUpdatedAt;

  /**
   *
   * Creation date.
   *
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   *
   * Last modification date.
   *
   */
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   *
   * Constructor required by JPA.
   *
   */
  protected Security() {
  }

  /**
   *
   * Creates a new instance.
   *
   * @param company Issuing company
   * @param name Name of the instrument
   * @param isin International securities identification number (ISIN)
   * @param type Type of financial instrument
   * @param currency reference currency (ISO 4217)
   * @param ticker ticker symbol used on the exchange
   * @param tradingCurrency trading currency (ISO 4217)
   * @param listingDate date of admission to trading
   * @param status trading status of the instrument
   *
   */
  public Security(Company company, String name, String isin, SecurityType type, String currency, String ticker,
      String tradingCurrency, LocalDate listingDate, SecurityStatus status) {
    this.company = company;
    this.name = name;
    this.isin = isin;
    this.type = type;
    this.currency = currency;
    this.ticker = ticker;
    this.tradingCurrency = tradingCurrency;
    this.listingDate = listingDate;
    this.status = status;
  }

  /**
   *
   * Initializes the audit dates before the first save.
   *
   */
  @PrePersist
  void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  /**
   *
   * Refreshes the modification date before an update.
   *
   */
  @PreUpdate
  void onUpdate() {
    updatedAt = LocalDateTime.now();
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
   * @return the issuing company
   *
   */
  public Company getCompany() {
    return company;
  }

  /**
   *
   * @return the name of the instrument
   *
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @param name the name of the instrument
   *
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   *
   * @return the international securities identification number (ISIN)
   *
   */
  public String getIsin() {
    return isin;
  }

  /**
   *
   * @param isin the international securities identification number (ISIN)
   *
   */
  public void setIsin(String isin) {
    this.isin = isin;
  }

  /**
   *
   * @return the type of financial instrument
   *
   */
  public SecurityType getType() {
    return type;
  }

  /**
   *
   * @param type the type of financial instrument
   *
   */
  public void setType(SecurityType type) {
    this.type = type;
  }

  /**
   *
   * @return the reference currency (ISO 4217)
   *
   */
  public String getCurrency() {
    return currency;
  }

  /**
   *
   * @param currency the reference currency (ISO 4217)
   *
   */
  public void setCurrency(String currency) {
    this.currency = currency;
  }

  /**
   *
   * @return the ticker symbol used on the exchange
   *
   */
  public String getTicker() {
    return ticker;
  }

  /**
   *
   * @param ticker the ticker symbol used on the exchange
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
   * @return the date of admission to trading
   *
   */
  public LocalDate getListingDate() {
    return listingDate;
  }

  /**
   *
   * @param listingDate the date of admission to trading
   *
   */
  public void setListingDate(LocalDate listingDate) {
    this.listingDate = listingDate;
  }

  /**
   *
   * @return the delisting date, or {@code null} while the security is listed
   *
   */
  public LocalDate getDelistingDate() {
    return delistingDate;
  }

  /**
   *
   * @param delistingDate the delisting date, or {@code null} when listed
   *
   */
  public void setDelistingDate(LocalDate delistingDate) {
    this.delistingDate = delistingDate;
  }

  /**
   *
   * @return the number of issued shares, if applicable
   *
   */
  public Long getIssuedShares() {
    return issuedShares;
  }

  /**
   *
   * @param issuedShares the number of issued shares, if applicable
   *
   */
  public void setIssuedShares(Long issuedShares) {
    this.issuedShares = issuedShares;
  }

  /**
   *
   * @return the number of outstanding shares, if applicable
   *
   */
  public Long getOutstandingShares() {
    return outstandingShares;
  }

  /**
   *
   * @param outstandingShares the number of outstanding shares, if applicable
   *
   */
  public void setOutstandingShares(Long outstandingShares) {
    this.outstandingShares = outstandingShares;
  }

  /**
   *
   * @return the status of the instrument
   *
   */
  public SecurityStatus getStatus() {
    return status;
  }

  /**
   *
   * @param status the status of the instrument
   *
   */
  public void setStatus(SecurityStatus status) {
    this.status = status;
  }

  /**
   *
   * @return the most recently executed price, in the trading currency
   *
   */
  public BigDecimal getCurrentPrice() {
    return currentPrice;
  }

  /**
   *
   * @return the date and time at which the current price was last updated
   *
   */
  public LocalDateTime getCurrentPriceUpdatedAt() {
    return currentPriceUpdatedAt;
  }

  /**
   *
   * Updates the most recently executed price on the exchange.
   *
   * @param currentPrice the price, in the trading currency
   * @param updatedAt the date and time of the price update
   *
   */
  public void updateCurrentPrice(BigDecimal currentPrice, LocalDateTime updatedAt) {
    this.currentPrice = currentPrice;
    this.currentPriceUpdatedAt = updatedAt;
  }

  /**
   *
   * @return the creation date
   *
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   *
   * @return the last modification date
   *
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   *
  /**
   *
   * Textual representation without lazy associations.
   *
   */
  @Override
  public String toString() {
    return "Security{id=" + id + ", name=" + name + ", isin=" + isin + ", ticker=" + ticker
        + ", type=" + type + ", currency=" + currency + ", status=" + status + "}";
  }
}
