package be.sgerard.demo.company.model.dto;

import be.sgerard.demo.company.model.CompanyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Company entity.
 *
 * Used for transferring company data in REST API requests and responses.
 */
@Schema(name = "Company", description = "Data Transfer Object representing a company that may issue securities traded on an exchange")
public class CompanyDto {

	/**
	 * Internal unique identifier.
	 */
	private UUID id;

	/**
	 * Commercial name.
	 */
	private String name;

	/**
	 * Country of the head office (ISO 3166-1 alpha-2).
	 */
	private String countryCode;

	/**
	 * Official website.
	 */
	private String website;

	/**
	 * URL of the logo.
	 */
	private String logoUrl;

	/**
	 * Reference to the industry.
	 */
	private UUID industryId;

	/**
	 * Status of the company.
	 */
	private CompanyStatus status;

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
	public CompanyDto() {
	}

	/**
	 * Constructor with basic fields.
	 *
	 * @param id the identifier
	 * @param name the commercial name
	 * @param countryCode the country code
	 * @param status the company status
	 */
	public CompanyDto(final UUID id, final String name, final String countryCode, final CompanyStatus status) {
		this.id = id;
		this.name = name;
		this.countryCode = countryCode;
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
	 * Gets the commercial name.
	 *
	 * @return the commercial name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the commercial name.
	 *
	 * @param name the commercial name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the country code.
	 *
	 * @return the country code
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * Sets the country code.
	 *
	 * @param countryCode the country code to set
	 */
	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * Gets the website.
	 *
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * Sets the website.
	 *
	 * @param website the website to set
	 */
	public void setWebsite(final String website) {
		this.website = website;
	}

	/**
	 * Gets the logo URL.
	 *
	 * @return the logo URL
	 */
	public String getLogoUrl() {
		return logoUrl;
	}

	/**
	 * Sets the logo URL.
	 *
	 * @param logoUrl the logo URL to set
	 */
	public void setLogoUrl(final String logoUrl) {
		this.logoUrl = logoUrl;
	}

	/**
	 * Gets the industry identifier.
	 *
	 * @return the industry identifier
	 */
	public UUID getIndustryId() {
		return industryId;
	}

	/**
	 * Sets the industry identifier.
	 *
	 * @param industryId the industry identifier to set
	 */
	public void setIndustryId(final UUID industryId) {
		this.industryId = industryId;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public CompanyStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(final CompanyStatus status) {
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
