package be.sgerard.demo.bank.stock.model.external;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * Position of the portfolio service; only the fields needed for a valuation are read.
 *
 * @param id identifier of the position
 * @param stockSymbol symbol of the stock
 * @param quantity number of held units
 *
 */
public record PositionDto(UUID id, String stockSymbol, BigDecimal quantity) {
}
