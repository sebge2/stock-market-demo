package be.sgerard.demo.bank.stock.model.external;

import java.util.List;
import java.util.UUID;

/**
 *
 * Portfolio of the portfolio service; only the fields needed for a valuation are read.
 *
 * @param id identifier of the portfolio
 * @param positions positions held by the portfolio
 *
 */
public record PortfolioDto(UUID id, List<PositionDto> positions) {
}
