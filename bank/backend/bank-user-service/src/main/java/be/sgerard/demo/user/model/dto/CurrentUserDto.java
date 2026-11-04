package be.sgerard.demo.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
  /** Email address. */
  @Schema(description = "Email address")
  private String email;

  /** @return the login name */
  public String getUsername() { return username; }
  /** @param username the login name */
  public void setUsername(String username) { this.username = username; }
  /** @return the email address */
  public String getEmail() { return email; }
  /** @param email the email address */
  public void setEmail(String email) { this.email = email; }
}
