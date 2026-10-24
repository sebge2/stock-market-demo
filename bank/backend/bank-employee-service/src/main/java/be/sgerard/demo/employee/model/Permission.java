package be.sgerard.demo.employee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * A fine-grained right (for instance APPROVE_TRANSACTION) that can be granted through roles.
 *
 */
@Entity
@Table(name = "permission")
public class Permission extends AbstractEntity {

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
   * Textual representation.
   *
   */
  @Override
  public String toString() {
    return "Permission{id=" + getId() + ", code=" + code + ", category=" + category + "}";
  }
}
