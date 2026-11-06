package be.sgerard.demo.bank.stock.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * Position as returned by the portfolio service; only the fields needed for a valuation are read.
 *
 * @param id identifier of the position
 * @param stockSymbol symbol of the stock
 * @param quantity number of held units
 *
 */
@Schema(hidden = true)
public record RemotePositionDto(UUID id, String stockSymbol, BigDecimal quantity) {
}
