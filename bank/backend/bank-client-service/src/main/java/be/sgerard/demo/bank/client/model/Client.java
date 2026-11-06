package be.sgerard.demo.bank.client.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * A client of the bank. Credentials and email are managed by the user service and only referenced
 * through {@link #userId}.
 *
 */
@Entity
@Table(name = "client")
public class Client {

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
   * First name.
   *
   */
  @NotBlank
  @Size(max = 100)
  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  /**
   *
   * Last name.
   *
   */
  @NotBlank
  @Size(max = 100)
  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  /**
   *
   * Type of the client (individual or company).
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private ClientType type;

  /**
   *
   * Status of the client.
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private ClientStatus status;

  /**
   *
   * Identifier of the user in the user service.
   *
   */
  @NotNull
  @Column(name = "user_id", nullable = false, unique = true, updatable = false)
  private UUID userId;

  /**
   *
   * Creation date, set automatically on first persist.
   *
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   *
   * Last update date, refreshed automatically on every update.
   *
   */
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   *
   * Constructor required by JPA.
   *
   */
  protected Client() {
  }

  /**
   *
   * Creates a new instance.
   *
   * @param firstName the first name
   * @param lastName the last name
   * @param userId the user-service identifier
   * @param type the type of client
   * @param status the status of the client
   *
   */
  public Client(String firstName, String lastName, UUID userId, ClientType type, ClientStatus status) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.userId = userId;
    this.type = type;
    this.status = status;
  }

  /**
   *
   * Initializes the audit timestamps.
   *
   */
  @PrePersist
  void onCreate() {
    final LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  /**
   *
   * Refreshes the update timestamp.
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
   * @return the first name
   *
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   *
   * @param firstName the new first name
   *
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   *
   * @return the last name
   *
   */
  public String getLastName() {
    return lastName;
  }

  /**
   *
   * @param lastName the new last name
   *
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   *
   * @return the user-service identifier
   *
   */
  public UUID getUserId() {
    return userId;
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
   * @return the last update date
   *
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   *
   * @return the type of client
   *
   */
  public ClientType getType() {
    return type;
  }

  /**
   *
   * @param type the new type of client
   *
   */
  public void setType(ClientType type) {
    this.type = type;
  }

  /**
   *
   * @return the status
   *
   */
  public ClientStatus getStatus() {
    return status;
  }

  /**
   *
   * @param status the new status
   *
   */
  public void setStatus(ClientStatus status) {
    this.status = status;
  }
}
