package be.sgerard.demo.company.model.dto;

import be.sgerard.demo.company.model.Company;
import be.sgerard.demo.company.model.CompanyStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * Read-only representation of a {@link Company}.
 *
 * @param id Identifier
 * @param name Commercial name
 * @param countryCode Country code
 * @param website Website
 * @param logoUrl Logo URL
 * @param industryId Industry identifier
 * @param status Status
 * @param createdAt Creation date
 * @param updatedAt Last modification date
 *
 */
public record CompanyDto(
    UUID id,
    String name,
    String countryCode,
    String website,
    String logoUrl,
    UUID industryId,
    CompanyStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  /**
   *
   * Creates the DTO of an entity.
   * 
   * @param e the entity
   * @return the DTO
   *
   */
  public static CompanyDto from(Company e) {
    return new CompanyDto(
        e.getId(),
        e.getName(),
        e.getCountryCode(),
        e.getWebsite(),
        e.getLogoUrl(),
        e.getIndustryId(),
        e.getStatus(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }
}
