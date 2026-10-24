package be.sgerard.demo.employee.repository;

import be.sgerard.demo.employee.model.Department;
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
}
