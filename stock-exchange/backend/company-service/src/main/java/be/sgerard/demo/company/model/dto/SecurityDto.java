package be.sgerard.demo.company.model.dto;

import be.sgerard.demo.company.model.Security;
import be.sgerard.demo.company.model.SecurityStatus;
import be.sgerard.demo.company.model.SecurityType;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * Read-only representation of a {@link Security}.
 *
 * @param id Identifier
 * @param companyId Issuing company identifier
 * @param name Name
 * @param isin ISIN
 * @param type Type
 * @param currency Currency
 * @param issuedShares Issued shares
 * @param outstandingShares Outstanding shares
 * @param status Status
 * @param createdAt Creation date
 * @param updatedAt Last modification date
 *
 */
public record SecurityDto(
    UUID id,
    UUID companyId,
    String name,
    String isin,
    SecurityType type,
    String currency,
    Long issuedShares,
    Long outstandingShares,
    SecurityStatus status,
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
  public static SecurityDto from(Security e) {
    return new SecurityDto(
        e.getId(),
        e.getCompany().getId(),
        e.getName(),
        e.getIsin(),
        e.getType(),
        e.getCurrency(),
        e.getIssuedShares(),
        e.getOutstandingShares(),
        e.getStatus(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }
}
