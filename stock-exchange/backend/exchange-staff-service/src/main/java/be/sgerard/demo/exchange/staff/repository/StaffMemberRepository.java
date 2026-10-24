package be.sgerard.demo.exchange.staff.repository;

import be.sgerard.demo.exchange.staff.model.StaffMember;
import be.sgerard.demo.exchange.staff.model.StaffMemberStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link StaffMember} entities.
 *
 */
public interface StaffMemberRepository extends JpaRepository<StaffMember, UUID> {

  /**
   *
   * Finds a staff member by email.
   *
   * @param email the email
   * @return the matching staff member, if any
   */
  Optional<StaffMember> findByEmail(String email);

  /**
   *
   * Finds the staff members having the given status.
   *
   * @param status the status
   * @return the matching staff members
   */
  List<StaffMember> findByStatus(StaffMemberStatus status);
}
