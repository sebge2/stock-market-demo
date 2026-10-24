package be.sgerard.demo.company.model.dto;

import be.sgerard.demo.company.model.PriceInterval;
import be.sgerard.demo.company.model.StockPrice;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * Read-only representation of a {@link StockPrice}.
 *
 * @param id Identifier
 * @param listingId Listing identifier
 * @param timestamp Measure date and time
 * @param open Opening price
 * @param high Highest price
 * @param low Lowest price
 * @param close Closing price
 * @param volume Traded volume
 * @param interval Time interval
 *
 */
public record StockPriceDto(
    UUID id,
    UUID listingId,
    LocalDateTime timestamp,
    BigDecimal open,
    BigDecimal high,
    BigDecimal low,
    BigDecimal close,
    long volume,
    PriceInterval interval) {

  /**
   *
   * Creates the DTO of an entity.
   * 
   * @param e the entity
   * @return the DTO
   *
   */
  public static StockPriceDto from(StockPrice e) {
    return new StockPriceDto(
        e.getId(),
        e.getListing().getId(),
        e.getTimestamp(),
        e.getOpen(),
        e.getHigh(),
        e.getLow(),
        e.getClose(),
        e.getVolume(),
        e.getInterval());
  }
}
