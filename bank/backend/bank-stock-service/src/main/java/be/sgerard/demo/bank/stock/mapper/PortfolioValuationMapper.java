package be.sgerard.demo.bank.stock.mapper;

import be.sgerard.demo.bank.stock.model.dto.PortfolioValuationDto;
import be.sgerard.demo.bank.stock.model.dto.PositionValuationDto;
import be.sgerard.demo.bank.stock.model.dto.QuotationDto;
import be.sgerard.demo.bank.stock.model.external.PositionDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 *
 * Builds valuation DTOs from the positions of a portfolio and their quotations. Amounts use a scale of
 * {@value #SCALE} with half-even (banker's) rounding.
 *
 */
@Component
public class PortfolioValuationMapper {

  /**
   *
   * Currency of all amounts (hardcoded for the MVP).
   *
   */
  public static final String CURRENCY = "EUR";

  /**
   *
   * Scale of the monetary values.
   *
   */
  public static final int SCALE = 4;

  /**
   *
   * Values a position.
   *
   * @param position the held position
   * @param quotation the quotation of the position's stock
   * @return the valuation of the position
   *
   */
  public PositionValuationDto toPositionValuation(PositionDto position, QuotationDto quotation) {
    final BigDecimal price = quotation.currentPrice().setScale(SCALE, RoundingMode.HALF_EVEN);
    final BigDecimal value = position.quantity().multiply(quotation.currentPrice()).setScale(SCALE,
        RoundingMode.HALF_EVEN);
    return new PositionValuationDto(position.id(), position.stockSymbol(), position.quantity(), price, value,
        quotation.timestamp());
  }

  /**
   *
   * Assembles the valuation of a portfolio; the total is the sum of the position values.
   *
   * @param portfolioId identifier of the portfolio
   * @param positions valuation of every position
   * @param valuedAt instant of the valuation
   * @return the portfolio valuation
   *
   */
  public PortfolioValuationDto toPortfolioValuation(UUID portfolioId, List<PositionValuationDto> positions,
      LocalDateTime valuedAt) {
    final BigDecimal total = positions.stream()
        .map(PositionValuationDto::positionValue)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(SCALE, RoundingMode.HALF_EVEN);
    return new PortfolioValuationDto(portfolioId, total, CURRENCY, valuedAt, List.copyOf(positions));
  }
}
