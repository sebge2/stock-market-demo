package be.sgerard.demo.employee.model.dto;

import be.sgerard.demo.employee.model.RoleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * Payload used to create or update a role and its permissions.
 *
 */
public class RoleRequestDto {

  /** Role name. */
  @NotBlank
  @Size(max = 100)
  private String name;
  /** Role description. */
  @Size(max = 500)
  private String description;
  /** Role status. */
  @NotNull
  private RoleStatus status;
  /** Permission identifiers assigned to the role. */
  @NotNull
  private Set<UUID> permissionIds = new LinkedHashSet<>();

  /** @return the role name */
  public String getName() { return name; }
  /** @param name the role name */
  public void setName(String name) { this.name = name; }
  /** @return the role description */
  public String getDescription() { return description; }
  /** @param description the role description */
  public void setDescription(String description) { this.description = description; }
  /** @return the role status */
  public RoleStatus getStatus() { return status; }
  /** @param status the role status */
  public void setStatus(RoleStatus status) { this.status = status; }
  /** @return the permission identifiers */
  public Set<UUID> getPermissionIds() { return permissionIds; }
  /** @param permissionIds the permission identifiers */
  public void setPermissionIds(Set<UUID> permissionIds) { this.permissionIds = permissionIds; }
}
