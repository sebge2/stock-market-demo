package be.sgerard.demo.exchange.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Verifies login users are loaded from the database with their roles and permissions.
 *
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class UserDetailsServiceTest {

  /** Service under test. */
  @Autowired
  private UserDetailsService userDetailsService;

  /**
   *
   * Verifies the admin user has the role authority and its permissions.
   *
   */
  @Test
  void loadsAdminAuthorities() {
    final UserDetails admin = userDetailsService.loadUserByUsername("admin");

    assertThat(admin.getAuthorities()).extracting(GrantedAuthority::getAuthority)
        .contains("ROLE_ADMIN", "ADMIN", "USER_CREATE");
  }

  /**
   *
   * Verifies the staff user only gets read permissions.
   *
   */
  @Test
  void loadsStaffAuthorities() {
    final UserDetails staff = userDetailsService.loadUserByUsername("staff");

    assertThat(staff.getAuthorities()).extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrder("ROLE_STAFF", "USER_LIST", "ROLE_LIST");
  }

  /**
   *
   * Verifies an unknown user is rejected.
   *
   */
  @Test
  void rejectsUnknownUser() {
    assertThatThrownBy(() -> userDetailsService.loadUserByUsername("ghost"))
        .isInstanceOf(UsernameNotFoundException.class);
  }
}
