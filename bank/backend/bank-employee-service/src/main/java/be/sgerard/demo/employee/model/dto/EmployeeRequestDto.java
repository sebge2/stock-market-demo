package be.sgerard.demo.employee.model.dto;

import be.sgerard.demo.employee.model.EmployeeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 *
 * Payload used to create or replace an employee. The linked user identifier is never provided by the
 * client: on creation the user is created in the user service and its identifier is stored.
 *
 */
@Schema(name = "EmployeeRequest")
public class EmployeeRequestDto {
  @NotBlank @Size(max = 100) private String firstName;
  @NotBlank @Size(max = 100) private String lastName;
  @NotBlank @Email @Size(max = 255) private String email;
  @NotNull private EmployeeStatus status;
  private UUID departmentId;

  /**
   *
   * Job title of the employee.
   *
   */
  @Size(max = 100)
  @Schema(description = "Job title of the employee")
  private String jobTitle;
  /** @return the first name */ public String getFirstName() { return firstName; }
  /** @param firstName the first name */ public void setFirstName(String firstName) { this.firstName = firstName; }
  /** @return the last name */ public String getLastName() { return lastName; }
  /** @param lastName the last name */ public void setLastName(String lastName) { this.lastName = lastName; }
  /** @return the email address */ public String getEmail() { return email; }
  /** @param email the email address */ public void setEmail(String email) { this.email = email; }
  /** @return the status */ public EmployeeStatus getStatus() { return status; }
  /** @param status the status */ public void setStatus(EmployeeStatus status) { this.status = status; }
  /** @return the department identifier */ public UUID getDepartmentId() { return departmentId; }
  /** @param departmentId the department identifier */ public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }

  /**
   *
   * @return the job title
   *
   */
  public String getJobTitle() {
    return jobTitle;
  }

  /**
   *
   * @param jobTitle the job title
   *
   */
  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }
}
