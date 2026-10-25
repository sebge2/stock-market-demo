package be.sgerard.demo.user.config;

import static org.assertj.core.api.Assertions.assertThat;

import be.sgerard.demo.user.model.Permission;
import be.sgerard.demo.user.model.Role;
import be.sgerard.demo.user.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Verifies authorization reference data for the local profile.
 *
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class LocalDataInitializerTest {

  /** Role repository. */
  @Autowired
  private RoleRepository roleRepository;

  /**
   *
   * Verifies the ADMIN role grants the ADMIN permission.
   *
   */
  @Test
  void loadsAdministratorAuthorization() {
    Role role = roleRepository.findByName("ADMIN").orElseThrow();

    assertThat(role.getPermissions()).extracting(Permission::getCode).containsExactly("ADMIN");
  }
}
