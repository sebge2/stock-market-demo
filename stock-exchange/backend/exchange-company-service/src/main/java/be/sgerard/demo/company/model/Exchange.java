package be.sgerard.demo.company.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * A marketplace on which financial instruments can be traded.
 *
 */
@Entity
@Table(name = "exchange")
public class Exchange {

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
   * Official name of the market
   *
   */
  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false)
  private String name;

  /**
   *
   * Market Identifier Code (ISO 10383)
   *
   */
  @NotBlank
  @Pattern(regexp = "[A-Z0-9]{4}")
  @Column(name = "mic", nullable = false, unique = true, length = 4)
  private String mic;

  /**
   *
   * Country of the market (ISO 3166-1 alpha-2)
   *
   */
  @NotBlank
  @Pattern(regexp = "[A-Z]{2}")
  @Column(name = "country_code", nullable = false, length = 2)
  private String countryCode;

  /**
   *
   * Time zone identifier (for instance Europe/Brussels)
   *
   */
  @NotBlank
  @Size(max = 64)
  @Column(name = "timezone", nullable = false, length = 64)
  private String timezone;

  /**
   *
   * Main currency (ISO 4217)
   *
   */
  @NotBlank
  @Pattern(regexp = "[A-Z]{3}")
  @Column(name = "currency", nullable = false, length = 3)
  private String currency;

  /**
   *
   * Status of the market
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private ExchangeStatus status;

  /**
   *
   * Constructor required by JPA.
   *
   */
  protected Exchange() {
  }

  /**
   *
   * Creates a new instance.
   *
   * @param name Official name of the market
   * @param mic Market Identifier Code (ISO 10383)
   * @param countryCode Country of the market (ISO 3166-1 alpha-2)
   * @param timezone Time zone identifier (for instance Europe/Brussels)
   * @param currency Main currency (ISO 4217)
   * @param status Status of the market
   *
   */
  public Exchange(String name, String mic, String countryCode, String timezone, String currency, ExchangeStatus status) {
    this.name = name;
    this.mic = mic;
    this.countryCode = countryCode;
    this.timezone = timezone;
    this.currency = currency;
    this.status = status;
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
   * @return the official name of the market
   *
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @param name the official name of the market
   *
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   *
   * @return the market Identifier Code (ISO 10383)
   *
   */
  public String getMic() {
    return mic;
  }

  /**
   *
   * @param mic the market Identifier Code (ISO 10383)
   *
   */
  public void setMic(String mic) {
    this.mic = mic;
  }

  /**
   *
   * @return the country of the market (ISO 3166-1 alpha-2)
   *
   */
  public String getCountryCode() {
    return countryCode;
  }

  /**
   *
   * @param countryCode the country of the market (ISO 3166-1 alpha-2)
   *
   */
  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  /**
   *
   * @return the time zone identifier (for instance Europe/Brussels)
   *
   */
  public String getTimezone() {
    return timezone;
  }

  /**
   *
   * @param timezone the time zone identifier (for instance Europe/Brussels)
   *
   */
  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  /**
   *
   * @return the main currency (ISO 4217)
   *
   */
  public String getCurrency() {
    return currency;
  }

  /**
   *
   * @param currency the main currency (ISO 4217)
   *
   */
  public void setCurrency(String currency) {
    this.currency = currency;
  }

  /**
   *
   * @return the status of the market
   *
   */
  public ExchangeStatus getStatus() {
    return status;
  }

  /**
   *
   * @param status the status of the market
   *
   */
  public void setStatus(ExchangeStatus status) {
    this.status = status;
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
    if (!(o instanceof Exchange other)) {
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
    return "Exchange{id=" + id + ", name=" + name + ", mic=" + mic + ", countryCode=" + countryCode + ", timezone=" + timezone + ", currency=" + currency + ", status=" + status + "}";
  }
}
