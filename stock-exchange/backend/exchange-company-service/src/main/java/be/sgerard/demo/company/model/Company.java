package be.sgerard.demo.company.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * A company that may issue securities traded on an exchange.
 *
 */
@Entity
@Table(name = "company")
public class Company {

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
     * Commercial name.
     *
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    /**
     *
     * Country of the head office (ISO 3166-1 alpha-2).
     *
     */
    @NotBlank
    @Pattern(regexp = "[A-Z]{2}")
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    /**
     *
     * Official website.
     *
     */
    @Size(max = 2048)
    @Column(name = "website", length = 2048)
    private String website;

    /**
     *
     * URL of the logo.
     *
     */
    @Size(max = 2048)
    @Column(name = "logo_url", length = 2048)
    private String logoUrl;

    /**
     *
     * Reference to the industry.
     *
     */
    @Column(name = "industry_id")
    private UUID industryId;

    /**
     *
     * Status of the company.
     *
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CompanyStatus status;

    /**
     *
     * Creation date in the system.
     *
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     *
     * Last modification date.
     *
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     *
     * Constructor required by JPA.
     *
     */
    protected Company() {
    }

    /**
     *
     * Creates a company.
     *
     * @param name        commercial name
     * @param countryCode ISO 3166-1 alpha-2 country code of the head office
     * @param status      status of the company
     */
    public Company(String name, String countryCode, CompanyStatus status) {
        this.name = name;
        this.countryCode = countryCode;
        this.status = status;
    }

    /**
     *
     * Initializes the audit dates before the first save.
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
     * Refreshes the modification date before an update.
     *
     */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     *
     * @return the identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     *
     * @return the commercial name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the commercial name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     *
     * @param countryCode the ISO 3166-1 alpha-2 country code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     *
     * @return the website
     */
    public String getWebsite() {
        return website;
    }

    /**
     *
     * @param website the website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     *
     * @return the logo URL
     */
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     *
     * @param logoUrl the logo URL
     */
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    /**
     *
     * @return the industry identifier
     */
    public UUID getIndustryId() {
        return industryId;
    }

    /**
     *
     * @param industryId the industry identifier
     */
    public void setIndustryId(UUID industryId) {
        this.industryId = industryId;
    }

    /**
     *
     * @return the status
     */
    public CompanyStatus getStatus() {
        return status;
    }

    /**
     *
     * @param status the status
     */
    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    /**
     *
     * @return the creation date
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @return the last modification date
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * Entities are equal when they share the same non-null identifier.
     *
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Company other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.id);
    }

    /**
     *
     * Constant hash code, stable across the entity lifecycle.
     *
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     *
     * Textual representation without lazy associations.
     *
     */
    @Override
    public String toString() {
        return "Company{id=" + id + ", name='" + name + "', countryCode='" + countryCode + "', status=" + status + "}";
    }
}
