package be.sgerard.demo.company.model.dto;

import be.sgerard.demo.company.model.SecurityStatus;
import be.sgerard.demo.company.model.SecurityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

/**
 *
 * Data used to create or update a security.
 *
 * @param companyId identifier of the issuing company
 * @param name name of the security
 * @param isin ISIN code
 * @param type type of instrument
 * @param currency currency of the security (ISO 4217)
 * @param ticker ticker symbol on the exchange
 * @param tradingCurrency trading currency (ISO 4217)
 * @param listingDate date of first listing
 * @param delistingDate date of delisting, if any
 * @param issuedShares number of issued shares, if applicable
 * @param outstandingShares number of outstanding shares, if applicable
 * @param status trading status
 */
@Schema(name = "SecurityRequest", description = "Data used to create or update a security")
public record SecurityRequestDto(
    @NotNull UUID companyId,
    @NotBlank @Size(max = 255) String name,
    @NotBlank @Pattern(regexp = "[A-Z]{2}[A-Z0-9]{9}[0-9]") String isin,
    @NotNull SecurityType type,
    @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency,
    @NotBlank @Size(max = 20) String ticker,
    @NotBlank @Pattern(regexp = "[A-Z]{3}") String tradingCurrency,
    @NotNull LocalDate listingDate,
    LocalDate delistingDate,
    @PositiveOrZero Long issuedShares,
    @PositiveOrZero Long outstandingShares,
    @NotNull SecurityStatus status) {
}
