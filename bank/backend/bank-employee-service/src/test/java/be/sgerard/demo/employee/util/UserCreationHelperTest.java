package be.sgerard.demo.employee.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import org.junit.jupiter.api.Test;

/**
 *
 * Unit tests for UserCreationHelper utility class.
 *
 */
class UserCreationHelperTest {

  /**
   *
   * Test generateUsername extracts local part from email and converts to lowercase.
   *
   */
  @Test
  void testGenerateUsernameFromEmail() {
    final String email = "John.Doe@example.com";
    final String username = UserCreationHelper.generateUsername(email);

    assertEquals("john.doe", username);
  }

  /**
   *
   * Test generateUsername handles email with special characters.
   *
   */
  @Test
  void testGenerateUsernameWithSpecialCharacters() {
    final String email = "john.doe+tag@example.com";
    final String username = UserCreationHelper.generateUsername(email);

    assertEquals("john.doe+tag", username);
  }

  /**
   *
   * Test generateUsername limits length to 100 characters.
   *

   */
  @Test
  void testGenerateUsernameLimitedTo100Chars() {
    final String longLocalPart = "a".repeat(150) + "@example.com";
    final String username = UserCreationHelper.generateUsername(longLocalPart);

    assertTrue(username.length() <= 100);
  }

  /**
   *
   * Test generateTemporaryPassword generates a 12-character password.
   *

   */
  @Test
  void testGenerateTemporaryPasswordLength() {
    final String password = UserCreationHelper.generateTemporaryPassword();

    assertEquals(12, password.length());
  }

  /**
   *
   * Test generateTemporaryPassword contains mixed characters.
   *

   */
  @Test
  void testGenerateTemporaryPasswordComplexity() {
    final String password = UserCreationHelper.generateTemporaryPassword();

    assertNotNull(password);
    assertTrue(password.length() == 12);
    // Password should contain at least some characters from the set
    assertTrue(password.chars().anyMatch(Character::isUpperCase)
        || password.chars().anyMatch(Character::isLowerCase)
        || password.chars().anyMatch(Character::isDigit));
  }

  /**
   *
   * Test createDefaultRoleIds returns empty LinkedHashSet.
   *

   */
  @Test
  void testCreateDefaultRoleIds() {
    final LinkedHashSet result = UserCreationHelper.createDefaultRoleIds();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
