package be.sgerard.demo.exchange.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import be.sgerard.demo.exchange.user.model.dto.UserDto;
import be.sgerard.demo.exchange.user.repository.UserAccountRepository;
import be.sgerard.demo.exchange.user.model.dto.UserRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Verifies the user management business rules against the seeded local database.
 *
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class UserServiceTest {

  /** Service under test. */
  @Autowired
  private UserService userService;
  /** User account persistence. */
  @Autowired
  private UserAccountRepository userAccountRepository;
  /** Password encoder. */
  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   *
   * Verifies the seeded admin and staff users are listed.
   *
   */
  @Test
  void listsSeededUsers() {
    assertThat(userService.findAll()).extracting(UserDto::getUsername).contains("admin", "staff");
  }

  /**
   *
   * Verifies a created user can be read back and has its password hashed.
   *
   */
  @Test
  void createsUserWithHashedPassword() {
    final UserDto created = userService.create(request("trader", "trader@exchange.local.test", "password1"));

    assertThat(userService.findById(created.getId()).getUsername()).isEqualTo("trader");
    assertThat(userAccountRepository.findByUsername("trader").orElseThrow().getPasswordHash())
        .isNotEqualTo("password1")
        .matches(hash -> passwordEncoder.matches("password1", hash));
  }

  /**
   *
   * Verifies a missing password is rejected with a bad request.
   *
   */
  @Test
  void rejectsMissingPassword() {
    assertThatThrownBy(() -> userService.create(request("nopass", "nopass@exchange.local.test", " ")))
        .isInstanceOfSatisfying(ResponseStatusException.class,
            exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
  }

  /**
   *
   * Verifies duplicated usernames are rejected with a conflict.
   *
   */
  @Test
  void rejectsDuplicatedUsername() {
    assertThatThrownBy(() -> userService.create(request("admin", "other@exchange.local.test", "password1")))
        .isInstanceOfSatisfying(ResponseStatusException.class,
            exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
  }

  /**
   *
   * Builds a creation request.
   *
   * @param username the username
   * @param email the email
   * @param password the clear password
   * @return the request
   *
   */
  private static UserRequestDto request(String username, String email, String password) {
    final UserRequestDto request = new UserRequestDto();
    request.setUsername(username);
    request.setFullName("Full " + username);
    request.setEmail(email);
    request.setPassword(password);
    return request;
  }
}
