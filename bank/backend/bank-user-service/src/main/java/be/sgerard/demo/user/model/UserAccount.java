package be.sgerard.demo.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
   * @param email email address
   * @param passwordHash BCrypt password hash
   */
  public UserAccount(UUID id, String username, String email, String passwordHash) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
  }

  /** @return the identifier */
  public UUID getId() { return id; }
  /** @return the login name */
  public String getUsername() { return username; }
  /** @return the password hash */
  public String getPasswordHash() { return passwordHash; }
  /** @return assigned roles */
  public Set<Role> getRoles() { return Collections.unmodifiableSet(roles); }
  /** @param role role to grant */
  public void addRole(Role role) { roles.add(role); }
}
