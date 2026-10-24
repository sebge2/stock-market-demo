package be.sgerard.demo.employee.config;

import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.EmployeeStatus;
import be.sgerard.demo.employee.model.Permission;
import be.sgerard.demo.employee.model.PermissionCategory;
import be.sgerard.demo.employee.model.Role;
import be.sgerard.demo.employee.model.RoleStatus;
import be.sgerard.demo.employee.repository.EmployeeRepository;
import be.sgerard.demo.employee.repository.PermissionRepository;
import be.sgerard.demo.employee.repository.RoleRepository;
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
   * Repository used to manage seeded roles.
   *
   */
  private final RoleRepository roleRepository;

  /**
   *
   * Repository used to manage seeded permissions.
   *
   */
  private final PermissionRepository permissionRepository;

  /**
   *
   * Creates the local data initializer.
   *
   * @param employeeRepository repository used to manage employees
   * @param roleRepository repository used to manage roles
   * @param permissionRepository repository used to manage permissions
   */
  public LocalDataInitializer(EmployeeRepository employeeRepository, RoleRepository roleRepository,
      PermissionRepository permissionRepository) {
    this.employeeRepository = employeeRepository;
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
  }

  /**
   *
   * Creates the local administrator, role, and permission when absent.
   *
   * @param args application startup arguments
   */
  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    Permission adminPermission = permissionRepository.findByCode("ADMIN")
        .orElseGet(() -> permissionRepository.save(
            new Permission("ADMIN", "Administrative access", PermissionCategory.ADMIN)));

    Role adminRole = roleRepository.findByName("ADMIN")
        .orElseGet(() -> roleRepository.save(
            new Role("ADMIN", "Administrator role", RoleStatus.ACTIVE)));
    adminRole.addPermission(adminPermission);
    roleRepository.save(adminRole);

    Employee administrator = employeeRepository.findByEmail("sebastien.gerard@local.test")
        .orElseGet(() -> new Employee("Sébastien", "Gérard", "sebastien.gerard@local.test",
            "$2a$10$7EqJtq98hPqEX7fNZaFWoO.e3oBQmt4E3hKKe3sLCECo4BrfRPYLy",
            EmployeeStatus.ACTIVE, null));
    administrator.addRole(adminRole);
    employeeRepository.save(administrator);
  }
}
