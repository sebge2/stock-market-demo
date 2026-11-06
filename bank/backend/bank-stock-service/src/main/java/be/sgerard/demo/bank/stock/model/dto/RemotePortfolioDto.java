package be.sgerard.demo.bank.stock.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/**
 *
 * Portfolio as returned by the portfolio service; only the fields needed for a valuation are read.
 *
 * @param id identifier of the portfolio
 * @param positions positions held by the portfolio
 *
 */
@Schema(hidden = true)
public record RemotePortfolioDto(UUID id, List<RemotePositionDto> positions) {
}
