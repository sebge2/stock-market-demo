package be.sgerard.demo.company.mapper;

import be.sgerard.demo.company.model.Company;
import be.sgerard.demo.company.model.Security;
import be.sgerard.demo.company.model.dto.SecurityDto;
import be.sgerard.demo.company.model.dto.SecurityRequestDto;
import org.springframework.stereotype.Component;

/**
 *
 * Maps securities to their DTO representation and applies request data on entities.
 *
 */
@Component
public class SecurityMapper {

  /**
   *
   * Converts a security to its DTO.
   *
   * @param security security to convert
   * @return the DTO
   */
  public SecurityDto toDto(final Security security) {
    return new SecurityDto(security.getId(), security.getCompany().getId(), security.getCompany().getName(),
        security.getName(), security.getIsin(), security.getType(), security.getCurrency(), security.getTicker(),
        security.getTradingCurrency(), security.getListingDate(), security.getDelistingDate(),
        security.getIssuedShares(), security.getOutstandingShares(), security.getStatus(), security.getCreatedAt(),
        security.getUpdatedAt());
  }

  /**
   *
   * Creates a security from request data.
   *
   * @param request the request data
   * @param company the issuing company
   * @return the new security
   */
  public Security toEntity(final SecurityRequestDto request, final Company company) {
    final Security security = new Security(company, request.name(), request.isin(), request.type(),
        request.currency(), request.ticker(), request.tradingCurrency(), request.listingDate(), request.status());
    security.setDelistingDate(request.delistingDate());
    security.setIssuedShares(request.issuedShares());
    security.setOutstandingShares(request.outstandingShares());
    return security;
  }

  /**
   *
   * Applies request data on an existing security; the issuing company never changes.
   *
   * @param request the request data
   * @param security the security to update
   */
  public void updateEntity(final SecurityRequestDto request, final Security security) {
    security.setName(request.name());
    security.setIsin(request.isin());
    security.setType(request.type());
    security.setCurrency(request.currency());
    security.setTicker(request.ticker());
    security.setTradingCurrency(request.tradingCurrency());
    security.setListingDate(request.listingDate());
    security.setDelistingDate(request.delistingDate());
    security.setIssuedShares(request.issuedShares());
    security.setOutstandingShares(request.outstandingShares());
    security.setStatus(request.status());
  }
}
