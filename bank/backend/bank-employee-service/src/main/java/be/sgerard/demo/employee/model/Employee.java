package be.sgerard.demo.employee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * An employee of the bank.
 *
 */
@Entity
@Table(name = "employee")
public class Employee {

  /**
   *
   * Internal unique identifier.
   *
   */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  /**
   *
   * Creation date, set automatically on first persist.
   *
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   *
   * Last update date, refreshed automatically on every update.
   *
   */
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   *
   * First name.
   *
   */
  @NotBlank
  @Size(max = 100)
  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  /**
   *
   * Last name.
   *
   */
  @NotBlank
  @Size(max = 100)
  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  /**
   *
   * Unique email address.
   *
   */
  @NotBlank
  @Size(max = 255)
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  /**
   *
   * Identifier of the user managed by the user service.
   *
   */
  @NotNull
  @Column(name = "user_id", nullable = false, unique = true, updatable = false)
  private UUID userId;

  /**
   *
   * Employment status.
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private EmployeeStatus status;

  /**
   *
   * Department of the employee.
   *
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id")
  private Department department;

  /**
   *
   * Constructor required by JPA.
   *
   */
  protected Employee() {
  }

  /**
   *
   * Creates a new instance.
   *
   * @param firstName First name
   * @param lastName Last name
   * @param email Unique email address
   * @param userId user-service identifier
   * @param status Employment status
   * @param department Department of the employee
   *
   */
  public Employee(String firstName, String lastName, String email, UUID userId,
      EmployeeStatus status, Department department) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.userId = userId;
    this.status = status;
    this.department = department;
  }

  /**
   *
   * @return the first name
   *
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   *
   * @param firstName the new first name
   *
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   *
   * @return the last name
   *
   */
  public String getLastName() {
    return lastName;
  }

  /**
   *
   * @param lastName the new last name
   *
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   *
   * @return the email address
   *
   */
  public String getEmail() {
    return email;
  }

  /**
   *
   * @param email the new email address
   *
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   *
   * @return the user-service identifier
   *
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   *
   * @return the employment status
   *
   */
  public EmployeeStatus getStatus() {
    return status;
  }

  /**
   *
   * @param status the new employment status
   *
   */
  public void setStatus(EmployeeStatus status) {
    this.status = status;
  }

  /**
   *
   * @return the department
   *
   */
  public Department getDepartment() {
    return department;
  }

  /**
   *
   * @param department the new department
   *
   */
  public void setDepartment(Department department) {
    this.department = department;
  }

  /**
   *
   * Initializes the audit timestamps.
   *
   */
  @PrePersist
  void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  /**
   *
   * Refreshes the update timestamp.
   *
   */
  @PreUpdate
  void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  /**
   *
   * @return the identifier
   *
   */
  public UUID getId() {
    return id;
  }

  /**
   *
   * @return the creation date
   *
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   *
   * @return the last update date
   *
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   *
   * Textual representation without lazy associations nor credentials.
   *
   */
  @Override
  public String toString() {
    return "Employee{id=" + getId() + ", email=" + email + ", status=" + status + "}";
  }
}
