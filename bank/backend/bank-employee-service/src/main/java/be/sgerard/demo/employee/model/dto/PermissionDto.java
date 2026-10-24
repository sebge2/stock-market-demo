package be.sgerard.demo.employee.model.dto;

import be.sgerard.demo.employee.model.PermissionCategory;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * Public representation of a permission.
 *
 */
public class PermissionDto {

  /** Permission identifier. */
  private UUID id;
  /** Permission code. */
  private String code;
  /** Permission description. */
  private String description;
  /** Permission category. */
  private PermissionCategory category;
  /** Creation timestamp. */
  private LocalDateTime createdAt;
  /** Last update timestamp. */
  private LocalDateTime updatedAt;

  /** @return the identifier */
  public UUID getId() { return id; }
  /** @param id the identifier */
  public void setId(UUID id) { this.id = id; }
  /** @return the code */
  public String getCode() { return code; }
  /** @param code the code */
  public void setCode(String code) { this.code = code; }
  /** @return the description */
  public String getDescription() { return description; }
  /** @param description the description */
  public void setDescription(String description) { this.description = description; }
  /** @return the category */
  public PermissionCategory getCategory() { return category; }
  /** @param category the category */
  public void setCategory(PermissionCategory category) { this.category = category; }
  /** @return the creation timestamp */
  public LocalDateTime getCreatedAt() { return createdAt; }
  /** @param createdAt the creation timestamp */
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  /** @return the update timestamp */
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  /** @param updatedAt the update timestamp */
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
