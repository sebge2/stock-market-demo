package be.sgerard.demo.employee.config;

import static org.assertj.core.api.Assertions.assertThat;

import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.Department;
import be.sgerard.demo.employee.repository.DepartmentRepository;
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
class LocalDataSqlTest {

  /** Employee repository. */
  @Autowired
  private EmployeeRepository employeeRepository;

  /**
   *
   * Verifies the CEO is linked to the authorization-server administrator account.
   *
   */
  @Test
  void loadsTheCeoWithUserReference() {
    Employee employee = employeeRepository.findByEmail("alexander.muller@gerardson.biz").orElseThrow();

    assertThat(employee.getFirstName()).isEqualTo("Alexander");
    assertThat(employee.getLastName()).isEqualTo("Müller");
    assertThat(employee.getUserId()).isEqualTo(UUID.fromString("72e91c48-68b6-4de7-9a40-21b9b6224202"));
    assertThat(employee.getDepartment().getName()).isEqualTo("Direction générale");
    assertThat(employee.getJobTitle()).isEqualTo("Chief Executive Officer");
    assertThat(employee.getDepartment().getManager().getId()).isEqualTo(employee.getId());
  }

  /** Department repository. */
  @Autowired
  private DepartmentRepository departmentRepository;

  /**
   *
   * Verifies the EuroTrade Bank organization: a root department with six sub-departments, 33 people in total,
   * every manager belonging to the department they manage.
   *
   */
  @Test
  void loadsTheEuroTradeOrganization() {
    final Department root = departmentRepository.findByName("Direction générale").orElseThrow();

    assertThat(root.getParent()).isNull();
    assertThat(root.getManager().getFirstName()).isEqualTo("Alexander");
    assertThat(departmentRepository.findAll()).hasSize(7);
    assertThat(departmentRepository.findAll()).filteredOn(d -> d != root).allSatisfy(d -> assertThat(d.getParent()).isSameAs(root));
    assertThat(departmentRepository.findAll()).allSatisfy(d -> assertThat(d.getManager().getDepartment()).isSameAs(d));
    assertThat(employeeRepository.count()).isEqualTo(33);
  }
}
