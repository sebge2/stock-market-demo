package be.sgerard.demo.employee.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import be.sgerard.demo.employee.model.Department;
import be.sgerard.demo.employee.model.DepartmentStatus;
import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.EmployeeStatus;
import be.sgerard.demo.employee.model.Permission;
import be.sgerard.demo.employee.model.PermissionCategory;
import be.sgerard.demo.employee.model.Role;
import be.sgerard.demo.employee.model.RoleStatus;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

/**
 *
 * Persistence tests of the employee, department, role and permission entities.
 *
 */
@DataJpaTest
class EmployeePersistenceTest {

  /**
   *
   * Employee repository.
   *
   */
  @Autowired
  private EmployeeRepository employees;

  /**
   *
   * Department repository.
   *
   */
  @Autowired
  private DepartmentRepository departments;

  /**
   *
   * Role repository.
   *
   */
  @Autowired
  private RoleRepository roles;

  /**
   *
   * Permission repository.
   *
   */
  @Autowired
  private PermissionRepository permissions;

  /**
   *
   * Entity manager used to clear the persistence context.
   *
   */
  @Autowired
  private TestEntityManager entityManager;

  private Department newDepartment(String name) {
    return departments.saveAndFlush(new Department(name, "desc", DepartmentStatus.ACTIVE));
  }

  private Employee newEmployee(String email, Department department) {
    return employees.saveAndFlush(
        new Employee("John", "Doe", email, "hash", EmployeeStatus.ACTIVE, department));
  }

  @Test
  void employeeGetsGeneratedIdAndTimestamps() {
    Employee saved = newEmployee("john@bank.be", newDepartment("Risk"));
    entityManager.clear();

    Employee found = employees.findByEmail("john@bank.be").orElseThrow();
    assertThat(found.getId()).isNotNull().isEqualTo(saved.getId());
    assertThat(found.getCreatedAt()).isNotNull();
    assertThat(found.getUpdatedAt()).isNotNull();
    assertThat(found.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
    assertThat(employees.findByEmail("unknown@bank.be")).isEmpty();
  }

  @Test
  void updateRefreshesUpdatedAtButNotCreatedAt() throws InterruptedException {
    Employee saved = newEmployee("john@bank.be", newDepartment("Risk"));
    var createdAt = saved.getCreatedAt();
    var updatedAt = saved.getUpdatedAt();
    Thread.sleep(5);

    saved.setStatus(EmployeeStatus.ON_LEAVE);
    employees.saveAndFlush(saved);

    assertThat(saved.getCreatedAt()).isEqualTo(createdAt);
    assertThat(saved.getUpdatedAt()).isAfter(updatedAt);
    assertThat(saved.getStatus()).isEqualTo(EmployeeStatus.ON_LEAVE);
  }

  @Test
  void employeesAreFoundByDepartment() {
    Department risk = newDepartment("Risk");
    Department legal = newDepartment("Legal");
    newEmployee("a@bank.be", risk);
    newEmployee("b@bank.be", risk);
    newEmployee("c@bank.be", legal);

    assertThat(employees.findByDepartmentId(risk.getId())).hasSize(2);
    assertThat(employees.findByDepartmentId(legal.getId())).hasSize(1);
  }

  @Test
  void duplicateEmailIsRejected() {
    Department department = newDepartment("Risk");
    newEmployee("john@bank.be", department);

    assertThatThrownBy(() -> newEmployee("john@bank.be", department))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void invalidEmployeeIsRejected() {
    Department department = newDepartment("Risk");

    assertThatThrownBy(() -> employees.saveAndFlush(
        new Employee(" ", "Doe", "x@bank.be", "hash", EmployeeStatus.ACTIVE, department)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> employees.saveAndFlush(
        new Employee("John", "Doe", "x@bank.be", "", EmployeeStatus.ACTIVE, department)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> employees.saveAndFlush(
        new Employee("John", "Doe", "x@bank.be", "hash", null, department)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> employees.saveAndFlush(
        new Employee("J".repeat(101), "Doe", "x@bank.be", "hash", EmployeeStatus.ACTIVE, department)))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  void toStringDoesNotExposePassword() {
    Employee employee = newEmployee("john@bank.be", newDepartment("Risk"));

    assertThat(employee.toString()).doesNotContain("hash");
  }

  @Test
  void departmentNameIsUniqueAndManagerIsOptional() {
    Department department = newDepartment("Risk");
    assertThat(department.getManager()).isNull();
    assertThat(departments.findByName("Risk")).contains(department);

    assertThatThrownBy(() -> newDepartment("Risk"))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void departmentManagerIsPersisted() {
    Department department = newDepartment("Risk");
    Employee manager = newEmployee("boss@bank.be", department);
    department.setManager(manager);
    departments.saveAndFlush(department);
    entityManager.clear();

    Department found = departments.findByName("Risk").orElseThrow();
    assertThat(found.getManager().getId()).isEqualTo(manager.getId());
  }

  @Test
  void invalidDepartmentIsRejected() {
    assertThatThrownBy(() -> departments.saveAndFlush(new Department("", null, DepartmentStatus.ACTIVE)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> departments.saveAndFlush(
        new Department("Risk", "d".repeat(501), DepartmentStatus.ACTIVE)))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  void roleCarriesPermissionsSharedBetweenRoles() {
    Permission approve = permissions.saveAndFlush(
        new Permission("APPROVE_TRANSACTION", "Approve", PermissionCategory.TRANSACTION));
    Role manager = new Role("MANAGER", "Manager", RoleStatus.ACTIVE);
    manager.addPermission(approve);
    Role auditor = new Role("AUDITOR", "Auditor", RoleStatus.ACTIVE);
    auditor.addPermission(approve);
    roles.saveAndFlush(manager);
    roles.saveAndFlush(auditor);
    entityManager.clear();

    Role found = roles.findByName("MANAGER").orElseThrow();
    assertThat(found.getPermissions()).extracting(Permission::getCode)
        .containsExactly("APPROVE_TRANSACTION");
    assertThat(roles.findByName("AUDITOR").orElseThrow().getPermissions()).hasSize(1);
  }

  @Test
  void roleNameIsUnique() {
    roles.saveAndFlush(new Role("MANAGER", null, RoleStatus.ACTIVE));

    assertThatThrownBy(() -> roles.saveAndFlush(new Role("MANAGER", null, RoleStatus.INACTIVE)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void permissionCodeIsUnique() {
    permissions.saveAndFlush(new Permission("VIEW_REPORTS", null, PermissionCategory.REPORT));

    assertThat(permissions.findByCode("VIEW_REPORTS")).isPresent();
    assertThatThrownBy(() -> permissions.saveAndFlush(
        new Permission("VIEW_REPORTS", null, PermissionCategory.REPORT)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void invalidPermissionIsRejected() {
    assertThatThrownBy(() -> permissions.saveAndFlush(new Permission(" ", null, PermissionCategory.AUDIT)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> permissions.saveAndFlush(new Permission("X", null, null)))
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  void employeeRolesArePersistedAndRemovable() {
    Role role = roles.saveAndFlush(new Role("MANAGER", null, RoleStatus.ACTIVE));
    Employee employee = newEmployee("john@bank.be", newDepartment("Risk"));
    employee.addRole(role);
    employees.saveAndFlush(employee);
    entityManager.clear();

    Employee found = employees.findByEmail("john@bank.be").orElseThrow();
    assertThat(found.getRoles()).extracting(Role::getName).containsExactly("MANAGER");

    found.removeRole(found.getRoles().iterator().next());
    employees.saveAndFlush(found);
    entityManager.clear();

    assertThat(employees.findByEmail("john@bank.be").orElseThrow().getRoles()).isEmpty();
    assertThat(roles.findByName("MANAGER")).isPresent();
  }
}
