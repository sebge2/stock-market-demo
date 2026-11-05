package be.sgerard.demo.employee.util;

import java.security.SecureRandom;
import java.util.LinkedHashSet;

/**
 *
 * Utility class for generating user credentials during employee creation.
 *
 */
public class UserCreationHelper {

  /**
   *
   * Characters used for password generation.
   *
   */
  private static final String PASSWORD_CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

  /**
   *
   * SecureRandom instance for generating random values.
   *
   */
  private static final SecureRandom RANDOM = new SecureRandom();

  /**
   *
   * Private constructor to prevent instantiation.
   *
   */
  private UserCreationHelper() {
  }

  /**
   *
   * Generates a username from an email address by extracting the local part (before @).
   * The username is converted to lowercase and limited to 100 characters.
   *
   * @param email the email address
   * @return the generated username
   *
   */
  public static String generateUsername(String email) {
    final String localPart = email.split("@")[0];
    return localPart.toLowerCase().substring(0, Math.min(localPart.length(), 100));
  }

  /**
   *
   * Generates a temporary password of 12 characters with mixed case, digits, and symbols.
   *
   * @return the generated password
   *
   */
  public static String generateTemporaryPassword() {
    final StringBuilder password = new StringBuilder(12);
    for (int i = 0; i < 12; i++) {
      password.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
    }
    return password.toString();
  }

  /**
   *
   * Creates an empty set of role IDs for newly created users.
   *
   * @return an empty LinkedHashSet of role IDs
   *
   */
  public static LinkedHashSet<java.util.UUID> createDefaultRoleIds() {
    return new LinkedHashSet<>();
  }
}
