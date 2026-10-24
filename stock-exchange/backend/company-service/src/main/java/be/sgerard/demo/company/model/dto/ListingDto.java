package be.sgerard.demo.company.model.dto;

import be.sgerard.demo.company.model.Listing;
import be.sgerard.demo.company.model.ListingStatus;
import java.time.LocalDate;
import java.util.UUID;

/**
 *
 * Read-only representation of a {@link Listing}.
 *
 * @param id Identifier
 * @param securityId Security identifier
 * @param exchangeId Exchange identifier
 * @param ticker Ticker symbol
 * @param tradingCurrency Trading currency
 * @param listingDate Listing date
 * @param delistingDate Delisting date
 * @param status Status
 * @param isPrimary Whether this is the primary listing
 *
 */
public record ListingDto(
    UUID id,
    UUID securityId,
    UUID exchangeId,
    String ticker,
    String tradingCurrency,
    LocalDate listingDate,
    LocalDate delistingDate,
    ListingStatus status,
    boolean isPrimary) {

  /**
   *
   * Creates the DTO of an entity.
   * 
   * @param e the entity
   * @return the DTO
   *
   */
  public static ListingDto from(Listing e) {
    return new ListingDto(
        e.getId(),
        e.getSecurity().getId(),
        e.getExchange().getId(),
        e.getTicker(),
        e.getTradingCurrency(),
        e.getListingDate(),
        e.getDelistingDate(),
        e.getStatus(),
        e.isPrimary());
  }
}
