package be.sgerard.demo.exchange.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * A user account authenticated by the authorization server.
 *
 */
@Entity
@Table(name = "user_account")
public class UserAccount {

  /** User-service identifier. */
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;
  /** Login name. */
  @NotBlank
  @Size(max = 100)
  @Column(name = "username", nullable = false, unique = true, length = 100)
  private String username;
  /** Full name (first and last names) displayed for the user. */
  @NotBlank
  @Size(max = 200)
  @Column(name = "full_name", nullable = false, length = 200)
  private String fullName;
  /** Email address. */
  @NotBlank
  @Email
  @Size(max = 255)
  @Column(name = "email", nullable = false, unique = true)
  private String email;
  /** BCrypt password hash. */
  @NotBlank
  @Column(name = "password_hash", nullable = false)
  private String passwordHash;
  /** Roles granted to this account. */
  /**
   *
   * Avatar, a PNG of at most 128x128 pixels encoded in base64.
   *
   */
  @Lob
  @Column(name = "avatar")
  private String avatar;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_account_role", joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new LinkedHashSet<>();

  /** Constructor required by JPA. */
  protected UserAccount() {
  }

  /**
   *
   * Creates a user account.
   *
   * @param id user-service identifier
   * @param username login name
   * @param fullName full name
   * @param email email address
   * @param passwordHash BCrypt password hash
   */
  public UserAccount(UUID id, String username, String fullName, String email, String passwordHash) {
    this.id = id;
    this.username = username;
    this.fullName = fullName;
    this.email = email;
    this.passwordHash = passwordHash;
  }

  /** @return the identifier */
  public UUID getId() { return id; }
  /** @return the login name */
  public String getUsername() { return username; }
  /** @return the full name */
  public String getFullName() { return fullName; }
  /** @return the email address */
  public String getEmail() { return email; }
  /** @return the password hash */
  public String getPasswordHash() { return passwordHash; }
  /** @return assigned roles */
  /**
   *
   * @return the base64 encoded PNG avatar, or {@code null} when there is none
   *
   */
  public String getAvatar() { return avatar; }

  /**
   *
   * @param avatar the base64 encoded PNG avatar, or {@code null} to remove it
   *
   */
  public void setAvatar(String avatar) { this.avatar = avatar; }

  public Set<Role> getRoles() { return Collections.unmodifiableSet(roles); }
  /** @param role role to grant */
  public void addRole(Role role) { roles.add(role); }

  /**
   *
   * @param username the new login name
   *
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   *
   * @param fullName the new full name
   *
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  /**
   *
   * @param email the new email address
   *
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   *
   * @param passwordHash the new BCrypt password hash
   *
   */
  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  /**
   *
   * @param role role to revoke
   *
   */
  public void removeRole(Role role) {
    roles.remove(role);
  }
}
