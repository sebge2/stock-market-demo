package be.sgerard.demo.employee.repository;

import be.sgerard.demo.employee.model.Department;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link Department} entities.
 *
 */
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

  /**
   *
   * Finds an entity by its name.
   *
   * @param name the name
   * @return the matching entity, if any
   */
  Optional<Department> findByName(String name);

  /**
   *
   * @param parentId identifier of a department
   * @return {@code true} when the department has at least one sub-department
   *
   */
  boolean existsByParentId(UUID parentId);

  /**
   *
   * @param managerId identifier of an employee
   * @return the departments managed by the employee
   *
   */
  List<Department> findByManagerId(UUID managerId);
}
