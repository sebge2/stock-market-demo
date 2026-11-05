package be.sgerard.demo.employee.model.dto;

import be.sgerard.demo.employee.model.DepartmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * Public representation of a department.
 *
 */
@Schema(name = "Department")
public class DepartmentDto {

  /**
   *
   * Identifier of the employee managing the department.
   *
   */
  @Schema(description = "Identifier of the employee managing the department")
  private UUID managerId;

  /**
   * Department identifier.
   */
  private UUID id;

  /**
   * Department name.
   */
  private String name;

  /**
   * Department description.
   */
  private String description;

  /**
   * Department status.
   */
  private DepartmentStatus status;

  /**
   * Creation timestamp.
   */
  private LocalDateTime createdAt;

  /**
   * Last update timestamp.
   */
  private LocalDateTime updatedAt;

  /**
   *
   * @return the department identifier
   *
   */
  public UUID getId() { return id; }

  /**
   *
   * @param id the department identifier
   *
   */
  public void setId(UUID id) { this.id = id; }

  /**
   *
   * @return the department name
   *
   */
  public String getName() { return name; }

  /**
   *
   * @param name the department name
   *
   */
  public void setName(String name) { this.name = name; }

  /**
   *
   * @return the department description
   *
   */
  public String getDescription() { return description; }

  /**
   *
   * @param description the department description
   *
   */
  public void setDescription(String description) { this.description = description; }

  /**
   *
   * @return the department status
   *
   */
  public DepartmentStatus getStatus() { return status; }

  /**
   *
   * @param status the department status
   *
   */
  public void setStatus(DepartmentStatus status) { this.status = status; }

  /**
   *
   * @return the creation timestamp
   *
   */
  public LocalDateTime getCreatedAt() { return createdAt; }

  /**
   *
   * @param createdAt the creation timestamp
   *
   */
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  /**
   *
   * @return the last update timestamp
   *
   */
  public LocalDateTime getUpdatedAt() { return updatedAt; }

  /**
   *
   * @param updatedAt the last update timestamp
   *
   */
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

  /**
   *
   * @return the identifier of the employee managing the department
   *
   */
  public UUID getManagerId() {
    return managerId;
  }

  /**
   *
   * @param managerId the identifier of the employee managing the department
   *
   */
  public void setManagerId(UUID managerId) {
    this.managerId = managerId;
  }
}
