package be.sgerard.demo.employee.config;

import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.EmployeeStatus;
import be.sgerard.demo.employee.repository.EmployeeRepository;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Loads developer-only reference data when the local profile is active.
 *
 */
@Component
@Profile("local")
public class LocalDataInitializer implements ApplicationRunner {

  /**
   *
   * Repository used to manage seeded employees.
   *
   */
  private final EmployeeRepository employeeRepository;

  /**
   *
   * Creates the local employee data initializer.
   *
   * @param employeeRepository repository used to manage employees
   */
  public LocalDataInitializer(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  /**
   *
   * Creates the local administrator employee when absent.
   *
   * @param args application startup arguments
   */
  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    Employee administrator = employeeRepository.findByEmail("sebastien.gerard@local.test")
        .orElseGet(() -> new Employee("Sébastien", "Gérard", "sebastien.gerard@local.test",
            UUID.fromString("72e91c48-68b6-4de7-9a40-21b9b6224001"),
            EmployeeStatus.ACTIVE, null));
    employeeRepository.save(administrator);
  }
}
