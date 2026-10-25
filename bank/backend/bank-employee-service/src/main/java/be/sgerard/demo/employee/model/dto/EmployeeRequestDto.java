package be.sgerard.demo.employee.model.dto;

import be.sgerard.demo.employee.model.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 *
 * Payload used to create or replace an employee.
 *
 */
public class EmployeeRequestDto {
  @NotBlank @Size(max = 100) private String firstName;
  @NotBlank @Size(max = 100) private String lastName;
  @NotBlank @Email @Size(max = 255) private String email;
  @NotNull private UUID userId;
  @NotNull private EmployeeStatus status;
  private UUID departmentId;
  /** @return the first name */ public String getFirstName() { return firstName; }
  /** @param firstName the first name */ public void setFirstName(String firstName) { this.firstName = firstName; }
  /** @return the last name */ public String getLastName() { return lastName; }
  /** @param lastName the last name */ public void setLastName(String lastName) { this.lastName = lastName; }
  /** @return the email address */ public String getEmail() { return email; }
  /** @param email the email address */ public void setEmail(String email) { this.email = email; }
  /** @return the user-service identifier */ public UUID getUserId() { return userId; }
  /** @param userId the user-service identifier */ public void setUserId(UUID userId) { this.userId = userId; }
  /** @return the status */ public EmployeeStatus getStatus() { return status; }
  /** @param status the status */ public void setStatus(EmployeeStatus status) { this.status = status; }
  /** @return the department identifier */ public UUID getDepartmentId() { return departmentId; }
  /** @param departmentId the department identifier */ public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
}
