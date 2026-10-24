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
import java.time.LocalDateTime;
import java.util.Objects;
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
   * Status of the instrument
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private SecurityStatus status;

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
   * @param currency Reference currency (ISO 4217)
   * @param status Status of the instrument
   *
   */
  public Security(Company company, String name, String isin, SecurityType type, String currency, SecurityStatus status) {
    this.company = company;
    this.name = name;
    this.isin = isin;
    this.type = type;
    this.currency = currency;
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
   * Entities are equal when they share the same non-null identifier.
   *
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Security other)) {
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
    return "Security{id=" + id + ", name=" + name + ", isin=" + isin + ", type=" + type + ", currency=" + currency + ", status=" + status + "}";
  }
}
