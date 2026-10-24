package be.sgerard.demo.employee.config;

import static org.assertj.core.api.Assertions.assertThat;

import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.Permission;
import be.sgerard.demo.employee.model.Role;
import be.sgerard.demo.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Verifies the initial data provided by the local Spring profile.
 *
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class LocalDataInitializerTest {

  /**
   *
   * Repository used to read the seeded employee.
   *
   */
  @Autowired
  private EmployeeRepository employeeRepository;

  /**
   *
   * Verifies the local administrator has the ADMIN role and permission.
   *
   */
  @Test
  void loadsSebastienGerardAsAdministrator() {
    Employee employee = employeeRepository.findByEmail("sebastien.gerard@local.test").orElseThrow();

    assertThat(employee.getFirstName()).isEqualTo("Sébastien");
    assertThat(employee.getLastName()).isEqualTo("Gérard");
    assertThat(employee.getRoles()).extracting(Role::getName).contains("ADMIN");
    assertThat(employee.getRoles()).flatExtracting(Role::getPermissions)
        .extracting(Permission::getCode).contains("ADMIN");
  }
}
