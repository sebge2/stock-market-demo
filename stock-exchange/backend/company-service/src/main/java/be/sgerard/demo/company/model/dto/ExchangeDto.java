package be.sgerard.demo.company.model.dto;

import be.sgerard.demo.company.model.Exchange;
import be.sgerard.demo.company.model.ExchangeStatus;
import java.util.UUID;

/**
 *
 * Read-only representation of a {@link Exchange}.
 *
 * @param id Identifier
 * @param name Name
 * @param mic Market Identifier Code
 * @param countryCode Country code
 * @param timezone Time zone
 * @param currency Currency
 * @param status Status
 *
 */
public record ExchangeDto(
    UUID id,
    String name,
    String mic,
    String countryCode,
    String timezone,
    String currency,
    ExchangeStatus status) {

  /**
   *
   * Creates the DTO of an entity.
   * 
   * @param e the entity
   * @return the DTO
   *
   */
  public static ExchangeDto from(Exchange e) {
    return new ExchangeDto(
        e.getId(),
        e.getName(),
        e.getMic(),
        e.getCountryCode(),
        e.getTimezone(),
        e.getCurrency(),
        e.getStatus());
  }
}
