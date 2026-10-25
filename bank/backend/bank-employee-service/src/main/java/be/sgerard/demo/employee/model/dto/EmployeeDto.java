package be.sgerard.demo.employee.model.dto;

import be.sgerard.demo.employee.model.EmployeeStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * Public representation of an employee without credential data.
 *
 */
public class EmployeeDto {
    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private EmployeeStatus status;
    private UUID departmentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * @return the identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * @param id the identifier
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the user-service identifier
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * @param userId the user-service identifier
     */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    /**
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the status
     */
    public EmployeeStatus getStatus() {
        return status;
    }

    /**
     * @param status the status
     */
    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    /**
     * @return the department identifier
     */
    public UUID getDepartmentId() {
        return departmentId;
    }

    /**
     * @param departmentId the department identifier
     */
    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt the update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
