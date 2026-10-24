package be.sgerard.demo.exchange.staff.repository;

import be.sgerard.demo.exchange.staff.model.StaffRole;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link StaffRole} entities.
 *
 */
public interface StaffRoleRepository extends JpaRepository<StaffRole, UUID> {

  /**
   *
   * Finds a role by name.
   *
   * @param name the name
   * @return the matching role, if any
   */
  Optional<StaffRole> findByName(String name);
}
