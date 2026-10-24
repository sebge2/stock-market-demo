package be.sgerard.demo.exchange.staff.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * A member of the stock exchange staff (trader, analyst, operations...).
 *
 */
@Entity
@Table(name = "staff_member")
public class StaffMember {

  /**
   *
   * Internal unique identifier.
   *
   */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  /**
   *
   * Creation date, set automatically on first persist.
   *
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   *
   * Last update date, refreshed automatically on every update.
   *
   */
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   *
   * First name of the staff member.
   *
   */
  @NotBlank
  @Size(max = 100)
  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  /**
   *
   * Last name of the staff member.
   *
   */
  @NotBlank
  @Size(max = 100)
  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  /**
   *
   * Unique email address of the staff member.
   *
   */
  @NotBlank
  @Size(max = 255)
  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;

  /**
   *
   * Status of the staff member.
   *
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private StaffMemberStatus status;

  /**
   *
   * Role of the staff member, if any.
   *
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "staff_role_id")
  private StaffRole staffRole;

  /**
   *
   * Constructor required by JPA.
   *
   */
  protected StaffMember() {
  }

  /**
   *
   * Creates a new instance.
   *
   * @param firstName First name
   * @param lastName Last name
   * @param email Unique email address
   * @param status Status of the staff member
   * @param staffRole Role of the staff member, may be null
   *
   */
  public StaffMember(String firstName, String lastName, String email, StaffMemberStatus status,
      StaffRole staffRole) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.status = status;
    this.staffRole = staffRole;
  }

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
   * @return the creation date
   *
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   *
   * @return the last update date
   *
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   *
   * @return the first name
   *
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   *
   * @param firstName the new first name
   *
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   *
   * @return the last name
   *
   */
  public String getLastName() {
    return lastName;
  }

  /**
   *
   * @param lastName the new last name
   *
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   *
   * @return the email
   *
   */
  public String getEmail() {
    return email;
  }

  /**
   *
   * @param email the new email
   *
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   *
   * @return the status
   *
   */
  public StaffMemberStatus getStatus() {
    return status;
  }

  /**
   *
   * @param status the new status
   *
   */
  public void setStatus(StaffMemberStatus status) {
    this.status = status;
  }

  /**
   *
   * @return the role, or null if none
   *
   */
  public StaffRole getStaffRole() {
    return staffRole;
  }

  /**
   *
   * @param staffRole the new role, may be null
   *
   */
  public void setStaffRole(StaffRole staffRole) {
    this.staffRole = staffRole;
  }

  /**
   *
   * Initializes the audit timestamps.
   *
   */
  @PrePersist
  void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  /**
   *
   * Refreshes the update timestamp.
   *
   */
  @PreUpdate
  void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  /**
   *
   * Textual representation without lazy associations.
   *
   */
  @Override
  public String toString() {
    return "StaffMember{id=" + id + ", email=" + email + ", status=" + status + "}";
  }
}
