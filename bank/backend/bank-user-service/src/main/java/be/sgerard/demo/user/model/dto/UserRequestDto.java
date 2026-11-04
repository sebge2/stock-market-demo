package be.sgerard.demo.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * Payload used to create or update a user account. The password is mandatory on creation and
 * optional on update (the current one is kept when absent).
 *
 */
@Schema(name = "UserRequest")
public class UserRequestDto {

  /**
   *
   * Login name.
   *
   */
  @NotBlank
  @Size(max = 100)
  @Schema(description = "Login name")
  private String username;

  /**
   *
   * Email address.
   *
   */
  @NotBlank
  @Email
  @Size(max = 255)
  @Schema(description = "Email address")
  private String email;

  /**
   *
   * Clear text password, hashed before being stored.
   *
   */
  @Size(min = 8, max = 100)
  @Schema(description = "Clear text password, hashed before being stored", accessMode = Schema.AccessMode.WRITE_ONLY)
  private String password;

  /**
   *
   * Identifiers of the roles granted to the user.
   *
   */
  @NotNull
  @Schema(description = "Identifiers of the roles granted to the user")
  private Set<UUID> roleIds = new LinkedHashSet<>();

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
   * @return the clear text password, or {@code null} when unchanged
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
