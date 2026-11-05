package be.sgerard.demo.bank.client.model.dto;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * Request payload for creating a user in the user-service.
 *
 */
public class UserServiceCreationRequestDto {

  /**
   *
   * Login name.
   *
   */
  private String username;

  /**
   *
   * Full name of the user.
   *
   */
  private String fullName;

  /**
   *
   * Email address.
   *
   */
  private String email;

  /**
   *
   * Clear text password.
   *
   */
  private String password;

  /**
   *
   * Identifiers of the roles granted to the user.
   *
   */
  private Set<UUID> roleIds = new LinkedHashSet<>();

  /**
   *
   * Creates a new instance.
   *
   */
  public UserServiceCreationRequestDto() {
  }

  /**
   *
   * Creates a new instance with required fields.
   *
   * @param username the login name
   * @param email the email address
   * @param password the clear text password
   * @param roleIds the role identifiers
   *
   */
  public UserServiceCreationRequestDto(String username, String fullName, String email, String password,
      Set<UUID> roleIds) {
    this.username = username;
    this.fullName = fullName;
    this.email = email;
    this.password = password;
    this.roleIds = roleIds;
  }

  /**
   *
   * @return the login name
   *
   */
  public String getUsername() {
    return username;
  }

  /**
   *
   * @param username the login name
   *
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   *
   * @return the email address
   *
   */
  /**
   *
   * @return the full name
   *
   */
  public String getFullName() {
    return fullName;
  }

  /**
   *
   * @param fullName the full name
   *
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  /**
   *
   * @return the email address
   *
   */
  public String getEmail() {
    return email;
  }

  /**
   *
   * @param email the email address
   *
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   *
   * @return the clear text password
   *
   */
  public String getPassword() {
    return password;
  }

  /**
   *
   * @param password the clear text password
   *
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   *
   * @return the role identifiers
   *
   */
  public Set<UUID> getRoleIds() {
    return roleIds;
  }

  /**
   *
   * @param roleIds the role identifiers
   *
   */
  public void setRoleIds(Set<UUID> roleIds) {
    this.roleIds = roleIds;
  }
}
