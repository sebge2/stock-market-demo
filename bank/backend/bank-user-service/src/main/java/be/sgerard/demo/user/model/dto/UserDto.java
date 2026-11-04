package be.sgerard.demo.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * Public representation of a user account (never exposes the password hash).
 *
 */
@Schema(name = "User")
public class UserDto {

  /**
   *
   * User identifier.
   *
   */
  @Schema(description = "User identifier")
  private UUID id;

  /**
   *
   * Login name.
   *
   */
  @Schema(description = "Login name")
  private String username;

  /**
   *
   * Email address.
   *
   */
  @Schema(description = "Email address")
  private String email;

  /**
   *
   * Identifiers of the roles granted to the user.
   *
   */
  @Schema(description = "Identifiers of the roles granted to the user")
  private Set<UUID> roleIds = new LinkedHashSet<>();

  /**
   *
   * @return the identifier
   *
   */
  public UUID getId() {
    return id;
  }

  /**
   *
   * @param id the identifier
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
