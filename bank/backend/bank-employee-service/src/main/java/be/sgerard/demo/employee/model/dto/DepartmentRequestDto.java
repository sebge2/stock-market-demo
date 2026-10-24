package be.sgerard.demo.employee.model.dto;

import be.sgerard.demo.employee.model.DepartmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * Payload used to create or update a department.
 *
 */
public class DepartmentRequestDto {

  /**
   *
   * Department's unique business name.
   *
   */
  @NotBlank
  @Size(max = 100)
  private String name;

  /**
   *
   * Optional description of the department.
   *
   */
  @Size(max = 500)
  private String description;

  /**
   *
   * Current operational status of the department.
   *
   */
  @NotNull
  private DepartmentStatus status;

  /**
   *
   * @return the department name
   *
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @param name the department name
   *
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   *
   * @return the department description
   *
   */
  public String getDescription() {
    return description;
  }

  /**
   *
   * @param description the department description
   *
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   *
   * @return the department status
   *
   */
  public DepartmentStatus getStatus() {
    return status;
  }

  /**
   *
   * @param status the department status
   *
   */
  public void setStatus(DepartmentStatus status) {
    this.status = status;
  }
}
