package be.sgerard.demo.bank.stock.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * Latest quotation of a stock, as published by the stock exchange.
 *
 * @param stockSymbol symbol of the stock
 * @param currentPrice latest known price
 * @param timestamp instant of the quotation
 * @param priceInterval interval of the price candle (for instance DAY)
 * @param open opening price of the interval
 * @param high highest price of the interval
 * @param low lowest price of the interval
 * @param close closing price of the interval
 * @param volume traded volume of the interval
 *
 */
@Schema(name = "Quotation")
public record QuotationDto(
    String stockSymbol,
    BigDecimal currentPrice,
    LocalDateTime timestamp,
    String priceInterval,
    BigDecimal open,
    BigDecimal high,
    BigDecimal low,
    BigDecimal close,
    long volume) {
}
