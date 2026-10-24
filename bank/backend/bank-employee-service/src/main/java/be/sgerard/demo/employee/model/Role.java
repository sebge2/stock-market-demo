package be.sgerard.demo.employee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * A named set of permissions that can be assigned to employees.
 *
 */
@Entity
@Table(name = "role")
public class Role extends AbstractEntity {

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
  private RoleStatus status;

  /**
   *
   * Permissions granted by the role. A permission can be shared by several roles.
   *
   */
  @ManyToMany
  @JoinTable(name = "role_permission",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id"))
  private final Set<Permission> permissions = new LinkedHashSet<>();

  /**
   *
   * Constructor required by JPA.
   *
   */
  protected Role() {
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
  public Role(String name, String description, RoleStatus status) {
    this.name = name;
    this.description = description;
    this.status = status;
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
  public RoleStatus getStatus() {
    return status;
  }

  /**
   *
   * @param status the new status
   *
   */
  public void setStatus(RoleStatus status) {
    this.status = status;
  }

  /**
   *
   * @return an unmodifiable view of the granted permissions
   *
   */
  public Set<Permission> getPermissions() {
    return Collections.unmodifiableSet(permissions);
  }

  /**
   *
   * Grants a permission.
   *
   * @param permission the permission to grant
   *
   */
  public void addPermission(Permission permission) {
    permissions.add(permission);
  }

  /**
   *
   * Revokes a permission.
   *
   * @param permission the permission to revoke
   *
   */
  public void removePermission(Permission permission) {
    permissions.remove(permission);
  }

  /**
   *
   * Textual representation without lazy associations.
   *
   */
  @Override
  public String toString() {
    return "Role{id=" + getId() + ", name=" + name + ", status=" + status + "}";
  }
}
