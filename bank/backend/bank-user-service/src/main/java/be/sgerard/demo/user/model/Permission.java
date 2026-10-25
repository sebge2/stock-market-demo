package be.sgerard.demo.user.model;

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
 * A fine-grained right (for instance APPROVE_TRANSACTION) that can be granted through roles.
 *
 */
@Entity
@Table(name = "permission")
public class Permission {

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
   * Unique code of the permission.
   *
   */
  @NotBlank
  @Size(max = 100)
  @Column(name = "code", nullable = false, unique = true, length = 100)
  private String code;

  /**
   *
   * Description of the permission.
   *
   */
  @Size(max = 500)
  @Column(name = "description", length = 500)
  private String description;

  /**
   *
   * Functional category.
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false, length = 20)
  private PermissionCategory category;

  /**
   *
   * Constructor required by JPA.
   *
   */
  protected Permission() {
  }

  /**
   *
   * Creates a new instance.
   *
   * @param code Unique code of the permission
   * @param description Description of the permission
   * @param category Functional category
   *
   */
  public Permission(String code, String description, PermissionCategory category) {
    this.code = code;
    this.description = description;
    this.category = category;
  }

  /**
   *
   * @return the unique code
   *
   */
  public String getCode() {
    return code;
  }

  /**
   *
   * @param code the new unique code
   *
   */
  public void setCode(String code) {
    this.code = code;
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
   * @return the functional category
   *
   */
  public PermissionCategory getCategory() {
    return category;
  }

  /**
   *
   * @param category the new functional category
   *
   */
  public void setCategory(PermissionCategory category) {
    this.category = category;
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
   * Textual representation.
   *
   */
  @Override
  public String toString() {
    return "Permission{id=" + getId() + ", code=" + code + ", category=" + category + "}";
  }
}
