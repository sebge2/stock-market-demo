package be.sgerard.demo.company.model.dto;

import be.sgerard.demo.company.model.ListingStatus;
import java.time.LocalDate;
import java.util.UUID;

/**
 *
 * Representation of the listing of a security on an exchange.
 *
 * @param id identifier of the listing
 * @param securityId identifier of the listed security
 * @param exchangeId identifier of the exchange
 * @param ticker ticker symbol used on the exchange
 * @param tradingCurrency trading currency (ISO 4217)
 * @param listingDate date of first listing
 * @param delistingDate date of delisting, if any
 * @param status status of the listing
 * @param isPrimary whether this is the primary listing of the security
 */
public record ListingDto(UUID id, UUID securityId, UUID exchangeId, String ticker, String tradingCurrency,
                         LocalDate listingDate, LocalDate delistingDate, ListingStatus status, boolean isPrimary) {
}
