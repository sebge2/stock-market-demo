package be.sgerard.demo.exchange.staff.model.dto;

import be.sgerard.demo.exchange.staff.model.StaffRoleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for StaffRole entity.
 *
 * Used for transferring staff role data in REST API requests and responses.
 */
@Schema(name = "StaffRole", description = "A role that can be assigned to staff members")
public class StaffRoleDto {

	/**
	 * Internal unique identifier.
	 */
	private UUID id;

	/**
	 * Unique name of the role.
	 */
	private String name;

	/**
	 * Description of the role.
	 */
	private String description;

	/**
	 * Status of the role.
	 */
	private StaffRoleStatus status;

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
	public StaffRoleDto() {
	}

	/**
	 * Constructor with basic fields.
	 *
	 * @param id the identifier
	 * @param name the unique name
	 * @param description the description
	 * @param status the role status
	 */
	public StaffRoleDto(final UUID id, final String name, final String description, final StaffRoleStatus status) {
		this.id = id;
		this.name = name;
		this.description = description;
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public StaffRoleStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(final StaffRoleStatus status) {
		this.status = status;
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
