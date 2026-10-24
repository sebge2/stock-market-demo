package be.sgerard.demo.exchange.staff.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import be.sgerard.demo.exchange.staff.model.StaffMember;
import be.sgerard.demo.exchange.staff.model.StaffMemberStatus;
import be.sgerard.demo.exchange.staff.model.StaffRole;
import be.sgerard.demo.exchange.staff.model.StaffRoleStatus;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

/**
 *
 * Persistence tests of the staff member and staff role entities.
 *
 */
@DataJpaTest
class StaffPersistenceTest {

  /**
   *
   * Staff member repository.
   *
   */
  @Autowired
  private StaffMemberRepository members;

  /**
   *
   * Staff role repository.
   *
   */
  @Autowired
  private StaffRoleRepository roles;

  /**
   *
   * Entity manager used to clear the persistence context.
   *
   */
  @Autowired
  private TestEntityManager entityManager;

  /**
   *
   * Persists and flushes an active role with the given name.
   *
   */
  private StaffRole newRole(String name) {
    return roles.saveAndFlush(new StaffRole(name, "desc", StaffRoleStatus.ACTIVE));
  }

  /**
   *
   * Persists and flushes an active member with the given email and role.
   *
   */
  private StaffMember newMember(String email, StaffRole role) {
    return members.saveAndFlush(
        new StaffMember("John", "Doe", email, StaffMemberStatus.ACTIVE, role));
  }

  /**
   *
   * Verifies that a member gets a generated id and audit timestamps, and can be found by email.
   *
   */
  @Test
  void memberGetsGeneratedIdAndTimestamps() {
    StaffMember saved = newMember("john@exchange.be", null);
    entityManager.clear();

    StaffMember found = members.findByEmail("john@exchange.be").orElseThrow();
    assertThat(found.getId()).isNotNull().isEqualTo(saved.getId());
    assertThat(found.getCreatedAt()).isNotNull();
    assertThat(found.getUpdatedAt()).isNotNull();
    assertThat(found.getStaffRole()).isNull();
    assertThat(members.findByEmail("unknown@exchange.be")).isEmpty();
  }

  /**
   *
   * Verifies that an update refreshes the update timestamp and keeps the creation timestamp.
   *
   */
  @Test
  void updateRefreshesUpdatedAtButNotCreatedAt() throws InterruptedException {
    StaffMember saved = newMember("john@exchange.be", null);
    var createdAt = saved.getCreatedAt();
    var updatedAt = saved.getUpdatedAt();
    Thread.sleep(5);

    saved.setStatus(StaffMemberStatus.ON_LEAVE);
    members.saveAndFlush(saved);

    assertThat(saved.getCreatedAt()).isEqualTo(createdAt);
    assertThat(saved.getUpdatedAt()).isAfter(updatedAt);
    assertThat(saved.getStatus()).isEqualTo(StaffMemberStatus.ON_LEAVE);
  }

  /**
   *
   * Verifies that two members cannot share the same email.
   *
   */
  @Test
  void duplicateEmailIsRejected() {
    newMember("john@exchange.be", null);

    assertThatThrownBy(() -> newMember("john@exchange.be", null))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  /**
   *
   * Verifies that blank, missing or too long member fields are rejected by validation.
   *
   */
  @Test
  void invalidMemberIsRejected() {
    assertThatThrownBy(() -> members.saveAndFlush(
        new StaffMember(" ", "Doe", "x@exchange.be", StaffMemberStatus.ACTIVE, null)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> members.saveAndFlush(
        new StaffMember("John", "", "x@exchange.be", StaffMemberStatus.ACTIVE, null)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> members.saveAndFlush(
        new StaffMember("John", "Doe", " ", StaffMemberStatus.ACTIVE, null)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> members.saveAndFlush(
        new StaffMember("John", "Doe", "x@exchange.be", null, null)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> members.saveAndFlush(
        new StaffMember("J".repeat(101), "Doe", "x@exchange.be", StaffMemberStatus.ACTIVE, null)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> members.saveAndFlush(new StaffMember("John", "Doe",
        "e".repeat(256), StaffMemberStatus.ACTIVE, null)))
        .isInstanceOf(ConstraintViolationException.class);
  }

  /**
   *
   * Verifies that members are retrieved by status.
   *
   */
  @Test
  void membersAreFoundByStatus() {
    newMember("a@exchange.be", null);
    newMember("b@exchange.be", null);
    StaffMember onLeave = newMember("c@exchange.be", null);
    onLeave.setStatus(StaffMemberStatus.ON_LEAVE);
    members.saveAndFlush(onLeave);

    assertThat(members.findByStatus(StaffMemberStatus.ACTIVE)).hasSize(2);
    assertThat(members.findByStatus(StaffMemberStatus.ON_LEAVE)).hasSize(1);
    assertThat(members.findByStatus(StaffMemberStatus.INACTIVE)).isEmpty();
  }

  /**
   *
   * Verifies that the role of a member is persisted, reloaded and can be removed.
   *
   */
  @Test
  void memberRoleIsPersistedAndOptional() {
    StaffRole trader = newRole("TRADER");
    newMember("john@exchange.be", trader);
    entityManager.clear();

    StaffMember found = members.findByEmail("john@exchange.be").orElseThrow();
    assertThat(found.getStaffRole().getId()).isEqualTo(trader.getId());
    assertThat(found.getStaffRole().getName()).isEqualTo("TRADER");

    found.setStaffRole(null);
    members.saveAndFlush(found);
    entityManager.clear();

    assertThat(members.findByEmail("john@exchange.be").orElseThrow().getStaffRole()).isNull();
    assertThat(roles.findByName("TRADER")).isPresent();
  }

  /**
   *
   * Verifies that a role gets generated id and timestamps, and is found by name.
   *
   */
  @Test
  void roleGetsGeneratedIdAndTimestampsAndIsFoundByName() {
    StaffRole saved = newRole("ANALYST");
    entityManager.clear();

    StaffRole found = roles.findByName("ANALYST").orElseThrow();
    assertThat(found.getId()).isNotNull().isEqualTo(saved.getId());
    assertThat(found.getCreatedAt()).isNotNull();
    assertThat(found.getUpdatedAt()).isNotNull();
    assertThat(found.getStatus()).isEqualTo(StaffRoleStatus.ACTIVE);
    assertThat(roles.findByName("UNKNOWN")).isEmpty();
  }

  /**
   *
   * Verifies that a role name is unique.
   *
   */
  @Test
  void roleNameIsUnique() {
    newRole("TRADER");

    assertThatThrownBy(() -> roles.saveAndFlush(new StaffRole("TRADER", null, StaffRoleStatus.INACTIVE)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  /**
   *
   * Verifies that a blank name, missing status or too long description is rejected for a role.
   *
   */
  @Test
  void invalidRoleIsRejected() {
    assertThatThrownBy(() -> roles.saveAndFlush(new StaffRole(" ", null, StaffRoleStatus.ACTIVE)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> roles.saveAndFlush(new StaffRole("TRADER", null, null)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> roles.saveAndFlush(
        new StaffRole("TRADER", "d".repeat(501), StaffRoleStatus.ACTIVE)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> roles.saveAndFlush(
        new StaffRole("n".repeat(101), null, StaffRoleStatus.ACTIVE)))
        .isInstanceOf(ConstraintViolationException.class);
  }
}
