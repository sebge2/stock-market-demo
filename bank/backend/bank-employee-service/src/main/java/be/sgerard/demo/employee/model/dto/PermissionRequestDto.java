package be.sgerard.demo.employee.model.dto;

import be.sgerard.demo.employee.model.PermissionCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * Payload used to create or update a permission.
 *
 */
public class PermissionRequestDto {

  /**
   * Permission code.
   */
  @NotBlank
  @Size(max = 100)
  private String code;

  /**
   * Permission description.
   */
  @Size(max = 500)
  private String description;

  /**
   * Permission category.
   */
  @NotNull
  private PermissionCategory category;

  /**
   * @return the permission code
   */
  public String getCode() { return code; }

  /**
   * @param code the permission code
   */
  public void setCode(String code) { this.code = code; }

  /**
   * @return the permission description
   */
  public String getDescription() { return description; }

  /**
   * @param description the permission description
   */
  public void setDescription(String description) { this.description = description; }

  /**
   * @return the permission category
   */
  public PermissionCategory getCategory() { return category; }

  /**
   * @param category the permission category
   */
  public void setCategory(PermissionCategory category) { this.category = category; }
}
