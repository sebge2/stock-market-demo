package be.sgerard.demo.employee.repository;

import be.sgerard.demo.employee.model.Employee;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link Employee} entities.
 *
 */
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

  /**
   *
   * Finds an employee by email.
   *
   * @param email the email
   * @return the matching employee, if any
   */
  Optional<Employee> findByEmail(String email);

  /**
   *
   * Finds the employees of a department.
   *
   * @param departmentId the department identifier
   * @return the employees of the department
   */
  List<Employee> findByDepartmentId(UUID departmentId);
}
