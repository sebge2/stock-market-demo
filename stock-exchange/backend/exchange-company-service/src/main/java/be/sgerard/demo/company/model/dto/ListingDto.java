package be.sgerard.demo.company.model.dto;

import be.sgerard.demo.company.model.ListingStatus;
import java.time.LocalDate;
import java.util.UUID;

/**
 *
 * Representation of the listing of a security on the stock exchange.
 *
 * @param id identifier of the listing
 * @param securityId identifier of the listed security
 * @param ticker ticker symbol
 * @param tradingCurrency trading currency (ISO 4217)
 * @param listingDate date of first listing
 * @param delistingDate date of delisting, if any
 * @param status status of the listing
 */
public record ListingDto(UUID id, UUID securityId, String ticker, String tradingCurrency,
                         LocalDate listingDate, LocalDate delistingDate, ListingStatus status) {
}
