package be.sgerard.demo.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * Representation of the currently authenticated user.
 *
 */
@Schema(name = "CurrentUser")
public class CurrentUserDto {

  /** Login name. */
  @Schema(description = "Login name")
  private String username;
  /**
   *
   * Full name.
   *
   */
  @Schema(description = "Full name")
  private String fullName;

  /** Email address. */
  @Schema(description = "Email address")
  private String email;
  /** Names (codes) of the permissions granted to the user through its roles. */
  @Schema(description = "Names of the permissions granted to the user through its roles")
  private Set<String> permissions = new LinkedHashSet<>();

  /** @return the login name */
  public String getUsername() { return username; }
  /** @param username the login name */
  public void setUsername(String username) { this.username = username; }
  /** @return the email address */
  public String getEmail() { return email; }
  /** @param email the email address */
  public void setEmail(String email) { this.email = email; }
  /** @return the permission names */
  public Set<String> getPermissions() { return permissions; }
  /** @param permissions the permission names */
  public void setPermissions(Set<String> permissions) { this.permissions = permissions; }

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
}
