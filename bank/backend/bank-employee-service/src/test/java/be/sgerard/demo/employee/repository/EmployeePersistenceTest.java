package be.sgerard.demo.employee.repository;

import static org.assertj.core.api.Assertions.assertThat;

import be.sgerard.demo.employee.model.Department;
import be.sgerard.demo.employee.model.DepartmentStatus;
import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.EmployeeStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 *
 * Verifies persistence of employee data without authorization data.
 *
 */
@DataJpaTest
class EmployeePersistenceTest {

  /** Employee repository. */
  @Autowired
  private EmployeeRepository employeeRepository;

  /** Department repository. */
  @Autowired
  private DepartmentRepository departmentRepository;

  /**
   *
   * Persists an employee linked to a Keycloak user.
   *
   */
  @Test
  void persistsEmployeeWithKeycloakUserReference() {
    Department department = departmentRepository.saveAndFlush(
        new Department("Risk", "Risk management", DepartmentStatus.ACTIVE));
    UUID keycloakUserId = UUID.randomUUID();
    Employee employee = employeeRepository.saveAndFlush(new Employee("John", "Doe", "john@bank.be",
        keycloakUserId, EmployeeStatus.ACTIVE, department));

    Employee found = employeeRepository.findByKeycloakUserId(keycloakUserId).orElseThrow();
    assertThat(found.getId()).isEqualTo(employee.getId());
    assertThat(found.getDepartment().getId()).isEqualTo(department.getId());
  }
}
