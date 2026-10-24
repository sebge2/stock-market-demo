package be.sgerard.demo.employee.model.dto;

import be.sgerard.demo.employee.model.RoleStatus;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * Public representation of a role and its permission identifiers.
 *
 */
public class RoleDto {

  /** Role identifier. */
  private UUID id;
  /** Role name. */
  private String name;
  /** Role description. */
  private String description;
  /** Role status. */
  private RoleStatus status;
  /** Permission identifiers. */
  private Set<UUID> permissionIds = new LinkedHashSet<>();
  /** Creation timestamp. */
  private LocalDateTime createdAt;
  /** Update timestamp. */
  private LocalDateTime updatedAt;

  /** @return the identifier */ public UUID getId() { return id; }
  /** @param id the identifier */ public void setId(UUID id) { this.id = id; }
  /** @return the name */ public String getName() { return name; }
  /** @param name the name */ public void setName(String name) { this.name = name; }
  /** @return the description */ public String getDescription() { return description; }
  /** @param description the description */ public void setDescription(String description) { this.description = description; }
  /** @return the status */ public RoleStatus getStatus() { return status; }
  /** @param status the status */ public void setStatus(RoleStatus status) { this.status = status; }
  /** @return the permission identifiers */ public Set<UUID> getPermissionIds() { return permissionIds; }
  /** @param permissionIds the permission identifiers */ public void setPermissionIds(Set<UUID> permissionIds) { this.permissionIds = permissionIds; }
  /** @return the creation timestamp */ public LocalDateTime getCreatedAt() { return createdAt; }
  /** @param createdAt the creation timestamp */ public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  /** @return the update timestamp */ public LocalDateTime getUpdatedAt() { return updatedAt; }
  /** @param updatedAt the update timestamp */ public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
