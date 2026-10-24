package be.sgerard.demo.exchange.staff.model;

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
 * A role that can be assigned to stock exchange staff members (trader, analyst, operations...).
 *
 */
@Entity
@Table(name = "staff_role")
public class StaffRole {

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
   * Unique name of the role.
   *
   */
  @NotBlank
  @Size(max = 100)
  @Column(name = "name", nullable = false, unique = true, length = 100)
  private String name;

  /**
   *
   * Description of the role.
   *
   */
  @Size(max = 500)
  @Column(name = "description", length = 500)
  private String description;

  /**
   *
   * Status of the role.
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private StaffRoleStatus status;

  /**
   *
   * Constructor required by JPA.
   *
   */
  protected StaffRole() {
  }

  /**
   *
   * Creates a new instance.
   *
   * @param name Unique name of the role
   * @param description Description of the role
   * @param status Status of the role
   *
   */
  public StaffRole(String name, String description, StaffRoleStatus status) {
    this.name = name;
    this.description = description;
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
   * @return the name
   *
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @param name the new name
   *
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   *
   * @return the description
   *
   */
  public String getDescription() {
    return description;
  }

  /**
   *
   * @param description the new description
   *
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   *
   * @return the status
   *
   */
  public StaffRoleStatus getStatus() {
    return status;
  }

  /**
   *
   * @param status the new status
   *
   */
  public void setStatus(StaffRoleStatus status) {
    this.status = status;
  }

  /**
   *
   * Initializes the audit timestamps.
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
   * Refreshes the update timestamp.
   *
   */
  @PreUpdate
  void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  /**
   *
   * Textual representation.
   *
   */
  @Override
  public String toString() {
    return "StaffRole{id=" + id + ", name=" + name + ", status=" + status + "}";
  }
}
