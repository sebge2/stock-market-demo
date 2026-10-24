package be.sgerard.demo.employee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * An organisational unit of the bank.
 *
 */
@Entity
@Table(name = "department")
public class Department extends AbstractEntity {

  /**
   *
   * Unique name of the department.
   *
   */
  @NotBlank
  @Size(max = 100)
  @Column(name = "name", nullable = false, unique = true, length = 100)
  private String name;

  /**
   *
   * Description of the department.
   *
   */
  @Size(max = 500)
  @Column(name = "description", length = 500)
  private String description;

  /**
   *
   * Status of the department.
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private DepartmentStatus status;

  /**
   *
   * Manager of the department (optional).
   *
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manager_id")
  private Employee manager;

  /**
   *
   * Constructor required by JPA.
   *
   */
  protected Department() {
  }

  /**
   *
   * Creates a new instance.
   *
   * @param name Unique name of the department
   * @param description Description of the department
   * @param status Status of the department
   *
   */
  public Department(String name, String description, DepartmentStatus status) {
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
  public DepartmentStatus getStatus() {
    return status;
  }

  /**
   *
   * @param status the new status
   *
   */
  public void setStatus(DepartmentStatus status) {
    this.status = status;
  }

  /**
   *
   * @return the manager, or {@code null} when there is none
   *
   */
  public Employee getManager() {
    return manager;
  }

  /**
   *
   * @param manager the new manager, or {@code null} to remove it
   *
   */
  public void setManager(Employee manager) {
    this.manager = manager;
  }

  /**
   *
   * Textual representation without lazy associations.
   *
   */
  @Override
  public String toString() {
    return "Department{id=" + getId() + ", name=" + name + ", status=" + status + "}";
  }
}
