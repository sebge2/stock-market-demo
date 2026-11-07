package be.sgerard.demo.user.config;

import static org.assertj.core.api.Assertions.assertThat;

import be.sgerard.demo.user.model.RoleStatus;
import be.sgerard.demo.user.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Verifies that inactive roles are not granted to the authenticated user, hence not copied into access tokens.
 *
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class InactiveRoleAuthoritiesTest {

  /** Loads the users at authentication. */
  @Autowired
  private UserDetailsService userDetailsService;

  /** Role repository. */
  @Autowired
  private RoleRepository roleRepository;

  /**
   *
   * Verifies the authorities of a role disappear once the role is inactive.
   *
   */
  @Test
  void ignoresInactiveRoles() {
    assertThat(userDetailsService.loadUserByUsername("sebastien.gerard").getAuthorities()).extracting(GrantedAuthority::getAuthority)
        .contains("ROLE_ADMIN", "USER_LIST");

    roleRepository.findByName("ADMIN").orElseThrow().setStatus(RoleStatus.INACTIVE);
    roleRepository.flush();

    assertThat(userDetailsService.loadUserByUsername("sebastien.gerard").getAuthorities()).extracting(GrantedAuthority::getAuthority)
        .doesNotContain("ROLE_ADMIN", "USER_LIST");
  }
}
