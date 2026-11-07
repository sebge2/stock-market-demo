package be.sgerard.demo.exchange.staff.model.dto;

import be.sgerard.demo.exchange.staff.model.StaffMemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for StaffMember entity.
 *
 * Used for transferring staff member data in REST API requests and responses.
 */
@Schema(name = "StaffMember", description = "A member of the stock exchange staff")
public class StaffMemberDto {

	/**
	 * Internal unique identifier.
	 */
	private UUID id;

	/**
	 * First name of the staff member.
	 */
	private String firstName;

	/**
	 * Last name of the staff member.
	 */
	private String lastName;

	/**
	 * Unique email address of the staff member.
	 */
	private String email;

	/**
	 * Status of the staff member.
	 */
	private StaffMemberStatus status;

	/**
	 * ID of the role assigned to this staff member.
	 */
	private UUID staffRoleId;

	/**
	 * Name of the role assigned to this staff member.
	 */
	private String staffRoleName;

	/**
	 * Creation date in the system.
	 */
	private LocalDateTime createdAt;

	/**
	 * Last modification date.
	 */
	private LocalDateTime updatedAt;

	/**
	 * Default constructor.
	 */
	public StaffMemberDto() {
	}

	/**
	 * Constructor with basic fields.
	 *
	 * @param id the identifier
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param email the email address
	 * @param status the staff member status
	 */
	public StaffMemberDto(final UUID id, final String firstName, final String lastName, final String email, final StaffMemberStatus status) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.status = status;
	}

	/**
	 * Gets the identifier.
	 *
	 * @return the identifier
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * Sets the identifier.
	 *
	 * @param id the identifier to set
	 */
	public void setId(final UUID id) {
		this.id = id;
	}

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName the first name to set
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the last name to set
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the email address.
	 *
	 * @return the email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email address.
	 *
	 * @param email the email address to set
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public StaffMemberStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(final StaffMemberStatus status) {
		this.status = status;
	}

	/**
	 * Gets the staff role identifier.
	 *
	 * @return the staff role identifier
	 */
	public UUID getStaffRoleId() {
		return staffRoleId;
	}

	/**
	 * Sets the staff role identifier.
	 *
	 * @param staffRoleId the staff role identifier to set
	 */
	public void setStaffRoleId(final UUID staffRoleId) {
		this.staffRoleId = staffRoleId;
	}

	/**
	 * Gets the staff role name.
	 *
	 * @return the staff role name
	 */
	public String getStaffRoleName() {
		return staffRoleName;
	}

	/**
	 * Sets the staff role name.
	 *
	 * @param staffRoleName the staff role name to set
	 */
	public void setStaffRoleName(final String staffRoleName) {
		this.staffRoleName = staffRoleName;
	}

	/**
	 * Gets the creation date.
	 *
	 * @return the creation date
	 */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	/**
	 * Sets the creation date.
	 *
	 * @param createdAt the creation date to set
	 */
	public void setCreatedAt(final LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Gets the last modification date.
	 *
	 * @return the last modification date
	 */
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * Sets the last modification date.
	 *
	 * @param updatedAt the last modification date to set
	 */
	public void setUpdatedAt(final LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
