package be.sgerard.demo.user.config;

import static org.assertj.core.api.Assertions.assertThat;

import be.sgerard.demo.user.model.Permission;
import be.sgerard.demo.user.model.Role;
import be.sgerard.demo.user.repository.RoleRepository;
import be.sgerard.demo.user.repository.UserAccountRepository;
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
   * User account repository.
   *
   */
  @Autowired
  private UserAccountRepository userAccountRepository;

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
        "PERMISSION_DELETE", "USER_LIST", "USER_CREATE", "USER_UPDATE", "USER_DELETE",
        "EMPLOYEE_LIST", "EMPLOYEE_CREATE", "EMPLOYEE_UPDATE", "EMPLOYEE_DELETE", "DEPARTMENT_LIST",
        "DEPARTMENT_CREATE", "DEPARTMENT_UPDATE", "DEPARTMENT_DELETE", "CLIENT_LIST", "CLIENT_CREATE",
        "CLIENT_UPDATE", "CLIENT_DELETE", "PORTFOLIO_LIST", "PORTFOLIO_CREATE", "PORTFOLIO_UPDATE",
        "PORTFOLIO_DELETE", "PORTFOLIO_VALUATION_READ", "QUOTATION_READ");
  }

  /**
   *
   * Verifies that the CRM role grants user listing, updating, and deletion to every Client Services
   * staff account loaded by the local reference data.
   *
   */
  @Test
  void loadsCrmAuthorizationForClientServices() {
    final Role role = roleRepository.findByName("CRM").orElseThrow();

    assertThat(role.getPermissions()).extracting(Permission::getCode)
        .containsExactlyInAnyOrder("USER_LIST", "USER_UPDATE", "USER_DELETE");
    assertThat(userAccountRepository.findByUsername("sophie.laurent").orElseThrow().getRoles())
        .containsExactly(role);
    assertThat(userAccountRepository.findByUsername("claire.moreau").orElseThrow().getRoles())
        .containsExactly(role);
    assertThat(userAccountRepository.findByUsername("pieter.janssen").orElseThrow().getRoles())
        .containsExactly(role);
    assertThat(userAccountRepository.findByUsername("elena.garcia").orElseThrow().getRoles())
        .containsExactly(role);
    assertThat(userAccountRepository.findByUsername("marco.bianchi").orElseThrow().getRoles())
        .containsExactly(role);
  }
}
