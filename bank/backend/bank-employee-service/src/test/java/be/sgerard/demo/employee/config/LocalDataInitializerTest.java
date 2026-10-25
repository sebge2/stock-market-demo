package be.sgerard.demo.employee.config;

import static org.assertj.core.api.Assertions.assertThat;

import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.repository.EmployeeRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Verifies local employee reference data.
 *
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class LocalDataInitializerTest {

  /** Employee repository. */
  @Autowired
  private EmployeeRepository employeeRepository;

  /**
   *
   * Verifies the local employee is linked to the authorization-server administrator.
   *
   */
  @Test
  void loadsSebastienGerardWithUserReference() {
    Employee employee = employeeRepository.findByEmail("sebastien.gerard@local.test").orElseThrow();

    assertThat(employee.getFirstName()).isEqualTo("Sébastien");
    assertThat(employee.getLastName()).isEqualTo("Gérard");
    assertThat(employee.getUserId())
        .isEqualTo(UUID.fromString("72e91c48-68b6-4de7-9a40-21b9b6224001"));
  }
}
