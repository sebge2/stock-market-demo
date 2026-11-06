package be.sgerard.demo.bank.stock.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 *
 * Current value of a portfolio, computed from the latest quotations.
 *
 * @param portfolioId identifier of the portfolio
 * @param totalValue sum of the value of all positions
 * @param currency currency of all amounts
 * @param valuedAt instant of the valuation
 * @param positions valuation of every position
 *
 */
@Schema(name = "PortfolioValuation")
public record PortfolioValuationDto(
    UUID portfolioId,
    BigDecimal totalValue,
    String currency,
    LocalDateTime valuedAt,
    List<PositionValuationDto> positions) {
}
