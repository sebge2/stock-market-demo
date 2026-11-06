package be.sgerard.demo.bank.stock.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * Valuation of a single position of a portfolio.
 *
 * @param positionId identifier of the position
 * @param stockSymbol symbol of the stock
 * @param quantity number of held units
 * @param currentPrice price used for the valuation
 * @param positionValue quantity multiplied by the current price
 * @param priceTimestamp instant of the price used for the valuation
 *
 */
@Schema(name = "PositionValuation")
public record PositionValuationDto(
    UUID positionId,
    String stockSymbol,
    BigDecimal quantity,
    BigDecimal currentPrice,
    BigDecimal positionValue,
    LocalDateTime priceTimestamp) {
}
