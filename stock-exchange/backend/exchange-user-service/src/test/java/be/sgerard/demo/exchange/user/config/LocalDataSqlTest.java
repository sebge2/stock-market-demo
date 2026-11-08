package be.sgerard.demo.exchange.user.config;

import static org.assertj.core.api.Assertions.assertThat;

import be.sgerard.demo.exchange.user.model.Permission;
import be.sgerard.demo.exchange.user.model.Role;
import be.sgerard.demo.exchange.user.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Verifies the authorization reference data loaded from the local SQL script.
 *
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class LocalDataSqlTest {

  /** Role repository. */
  @Autowired
  private RoleRepository roleRepository;

  /**
   *
   * Verifies the ADMIN role grants the administration and role permissions.
   *
   */
  @Test
  void loadsAdministratorAuthorization() {
    Role role = roleRepository.findByName("ADMIN").orElseThrow();

    assertThat(role.getPermissions()).extracting(Permission::getCode).containsExactlyInAnyOrder("ADMIN", "ROLE_LIST", "ROLE_CREATE",
        "ROLE_UPDATE", "ROLE_DELETE", "PERMISSION_LIST", "PERMISSION_CREATE", "PERMISSION_UPDATE",
        "PERMISSION_DELETE", "USER_LIST", "USER_CREATE", "USER_UPDATE", "USER_DELETE", "STAFF_LIST", "STAFF_CREATE",
        "STAFF_UPDATE", "STAFF_DELETE", "STAFF_ROLE_LIST", "STAFF_ROLE_CREATE", "STAFF_ROLE_UPDATE",
        "STAFF_ROLE_DELETE", "COMPANY_LIST", "COMPANY_CREATE", "COMPANY_UPDATE", "COMPANY_DELETE");
  }

  /**
   *
   * Verifies the STAFF role is seeded with its operational permissions.
   *
   */
  @Test
  void loadsStaffAuthorization() {
    assertThat(roleRepository.findByName("STAFF").orElseThrow().getPermissions()).extracting(Permission::getCode)
        .containsExactlyInAnyOrder("USER_LIST", "ROLE_LIST", "STAFF_LIST", "STAFF_ROLE_LIST", "COMPANY_LIST");
  }
}
