package be.sgerard.demo.employee.repository;

import be.sgerard.demo.employee.model.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link Role} entities.
 *
 */
public interface RoleRepository extends JpaRepository<Role, UUID> {

  /**
   *
   * Finds an entity by its name.
   *
   * @param name the name
   * @return the matching entity, if any
   */
  Optional<Role> findByName(String name);
}
