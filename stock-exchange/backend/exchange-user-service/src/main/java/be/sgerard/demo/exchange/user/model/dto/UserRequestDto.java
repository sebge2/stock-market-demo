package be.sgerard.demo.exchange.user.model.dto;

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
   * Full name.
   *
   */
  @NotBlank
  @Size(max = 200)
  @Schema(description = "Full name")
  private String fullName;

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
   * Source image of the avatar, encoded in base64 (a {@code data:} URI is accepted). It is reduced to
   * 128x128 pixels at most and stored as a base64 PNG. Absent means unchanged, an empty value removes the avatar.
   *
   */
  @Size(max = 7_000_000)
  @Schema(description = "Avatar image encoded in base64 (data URI accepted), reduced to 128x128 pixels at most. "
      + "Absent keeps the current avatar, empty removes it")
  private String avatar;

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
   * @return the source avatar image encoded in base64, or {@code null} when unchanged
   *
   */
  public String getAvatar() {
    return avatar;
  }

  /**
   *
   * @param avatar the source avatar image encoded in base64
   *
   */
  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
}
