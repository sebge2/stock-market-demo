package be.sgerard.demo.company.model.dto;

import be.sgerard.demo.company.model.SecurityStatus;
import be.sgerard.demo.company.model.SecurityType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * Representation of a security (a financial instrument issued by a company and traded on the stock exchange).
 *
 * @param id identifier of the security
 * @param companyId identifier of the issuing company
 * @param companyName name of the issuing company
 * @param name name of the security
 * @param isin ISIN code
 * @param type type of instrument
 * @param currency currency of the security (ISO 4217)
 * @param ticker ticker symbol on the exchange
 * @param tradingCurrency trading currency (ISO 4217)
 * @param listingDate date of first listing
 * @param delistingDate date of delisting, if any
 * @param issuedShares number of shares created by the company, including treasury shares, if applicable
 * @param outstandingShares number of shares held by third parties, excluding treasury shares, if applicable
 * @param freeFloatShares number of outstanding shares freely available for trading, if applicable
 * @param status trading status
 * @param lastPrice price of the most recent trade, in the trading currency
 * @param lastTradeAt date and time of the most recent trade
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
@Schema(name = "Security", description = "A financial instrument issued by a company and traded on the stock exchange")
public record SecurityDto(UUID id, UUID companyId, String companyName, String name, String isin, SecurityType type,
                          String currency, String ticker, String tradingCurrency, LocalDate listingDate,
                          LocalDate delistingDate, Long issuedShares, Long outstandingShares, Long freeFloatShares,
                          SecurityStatus status, BigDecimal lastPrice, LocalDateTime lastTradeAt, LocalDateTime createdAt,
                          LocalDateTime updatedAt) {
}
