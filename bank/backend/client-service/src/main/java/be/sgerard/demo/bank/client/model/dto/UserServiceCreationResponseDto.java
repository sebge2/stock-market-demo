package be.sgerard.demo.bank.client.model.dto;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * Response payload received when creating a user in the user-service.
 *
 */
public class UserServiceCreationResponseDto {

  /**
   *
   * User identifier.
   *
   */
  private UUID id;

  /**
   *
   * Login name.
   *
   */
  private String username;

  /**
   *
   * Email address.
   *
   */
  private String email;

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
  public UserServiceCreationResponseDto() {
  }

  /**
   *
   * Creates a new instance with required fields.
   *
   * @param id the user identifier
   * @param username the login name
   * @param email the email address
   * @param roleIds the role identifiers
   *
   */
  public UserServiceCreationResponseDto(UUID id, String username, String email,
      Set<UUID> roleIds) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.roleIds = roleIds;
  }

  /**
   *
   * @return the user identifier
   *
   */
  public UUID getId() {
    return id;
  }

  /**
   *
   * @param id the user identifier
   *
   */
  public void setId(UUID id) {
    this.id = id;
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
