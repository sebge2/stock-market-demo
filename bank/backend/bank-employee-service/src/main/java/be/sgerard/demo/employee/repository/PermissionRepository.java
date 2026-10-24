package be.sgerard.demo.employee.repository;

import be.sgerard.demo.employee.model.Permission;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link Permission} entities.
 *
 */
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

  /**
   *
   * Finds an entity by its code.
   *
   * @param code the code
   * @return the matching entity, if any
   */
  Optional<Permission> findByCode(String code);
}
