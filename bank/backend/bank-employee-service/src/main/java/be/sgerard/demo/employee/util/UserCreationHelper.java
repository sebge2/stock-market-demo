package be.sgerard.demo.employee.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

/**
 *
 * Utility class for generating user credentials during employee creation.
 *
 */
public class UserCreationHelper {

  /**
   *
   * Character classes a generated password draws from: upper case, lower case, digits and symbols.
   *
   */
  private static final String[] CHARACTER_CLASSES = {
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz", "0123456789", "!@#$%^&*"};

  /**
   *
   * Length of the generated passwords.
   *
   */
  private static final int PASSWORD_LENGTH = 12;

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
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email is required to generate a username");
    }
    final String localPart = email.trim().split("@", -1)[0].toLowerCase();
    if (localPart.isEmpty()) {
      throw new IllegalArgumentException("Email has an empty local part: " + email);
    }
    return localPart.substring(0, Math.min(localPart.length(), 100));
  }

  /**
   *
   * Generates a temporary password of 12 characters containing at least one upper case letter, one
   * lower case letter, one digit and one symbol.
   *
   * @return the generated password
   *
   */
  public static String generateTemporaryPassword() {
    final List<Character> chars = new ArrayList<>(PASSWORD_LENGTH);
    for (final String characterClass : CHARACTER_CLASSES) {
      chars.add(characterClass.charAt(RANDOM.nextInt(characterClass.length())));
    }
    final String all = String.join("", CHARACTER_CLASSES);
    while (chars.size() < PASSWORD_LENGTH) {
      chars.add(all.charAt(RANDOM.nextInt(all.length())));
    }
    Collections.shuffle(chars, RANDOM);
    final StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
    chars.forEach(password::append);
    return password.toString();
  }

  /**
   *
   * Creates an empty set of role IDs for newly created users.
   *
   * @return an empty LinkedHashSet of role IDs
   *
   */
  public static LinkedHashSet<UUID> createDefaultRoleIds() {
    return new LinkedHashSet<>();
  }
}
