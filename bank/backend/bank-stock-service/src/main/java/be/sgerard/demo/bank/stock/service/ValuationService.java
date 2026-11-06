package be.sgerard.demo.bank.stock.service;

import be.sgerard.demo.bank.stock.client.ExchangeServiceClient;
import be.sgerard.demo.bank.stock.exception.QuotationNotFoundException;
import be.sgerard.demo.bank.stock.exception.ValuationFailedException;
import be.sgerard.demo.bank.stock.mapper.PortfolioValuationMapper;
import be.sgerard.demo.bank.stock.model.dto.PortfolioValuationDto;
import be.sgerard.demo.bank.stock.model.dto.PositionValuationDto;
import be.sgerard.demo.bank.stock.model.dto.QuotationDto;
import be.sgerard.demo.bank.stock.model.external.PortfolioDto;
import be.sgerard.demo.bank.stock.model.external.PositionDto;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * Business logic computing the value of a portfolio from the latest stock quotations. A portfolio is never
 * partially valued: a missing or stale quotation fails the whole valuation.
 *
 */
@Service
public class ValuationService {

  /**
   *
   * Client of the stock exchange.
   *
   */
  private final ExchangeServiceClient exchangeClient;

  /**
   *
   * Mapper building the valuation DTOs.
   *
   */
  private final PortfolioValuationMapper mapper;

  /**
   *
   * Clock used to timestamp valuations and detect stale quotations.
   *
   */
  private final Clock clock;

  /**
   *
   * Maximum accepted age of a quotation.
   *
   */
  private final Duration quotationMaxAge;

  /**
   *
   * Creates a new instance.
   *
   * @param exchangeClient client of the stock exchange
   * @param mapper mapper building the valuation DTOs
   * @param clock clock of the application
   * @param quotationMaxAge maximum accepted age of a quotation
   *
   */
  public ValuationService(ExchangeServiceClient exchangeClient, PortfolioValuationMapper mapper, Clock clock,
      @Value("${bank.stock-service.quotation-max-age:PT24H}") Duration quotationMaxAge) {
    this.exchangeClient = exchangeClient;
    this.mapper = mapper;
    this.clock = clock;
    this.quotationMaxAge = quotationMaxAge;
  }

  /**
   *
   * Values a portfolio with the latest quotation of each of its positions.
   *
   * @param portfolio the portfolio to value
   * @return the valuation
   * @throws ValuationFailedException if a quotation is missing or older than the accepted age
   *
   */
  public PortfolioValuationDto valuate(PortfolioDto portfolio) {
    final LocalDateTime valuedAt = LocalDateTime.now(clock);
    final List<PositionDto> holdings = portfolio.positions() == null ? List.of() : portfolio.positions();
    final Map<String, QuotationDto> quotations = new HashMap<>();

    final List<PositionValuationDto> positions = holdings.stream()
        .map(position -> mapper.toPositionValuation(position,
            quotations.computeIfAbsent(position.stockSymbol(), symbol -> loadFreshQuotation(symbol, valuedAt))))
        .toList();

    return mapper.toPortfolioValuation(portfolio.id(), positions, valuedAt);
  }

  /**
   *
   * Retrieves the latest quotation of a stock.
   *
   * @param stockSymbol symbol of the stock
   * @return the latest quotation
   *
   */
  public QuotationDto findQuotation(String stockSymbol) {
    return exchangeClient.findLatestQuotation(stockSymbol);
  }

  /**
   *
   * Retrieves a quotation usable for a valuation.
   *
   * @param stockSymbol symbol of the stock
   * @param now instant of the valuation
   * @return the quotation
   * @throws ValuationFailedException if the quotation is missing or stale
   *
   */
  private QuotationDto loadFreshQuotation(String stockSymbol, LocalDateTime now) {
    final QuotationDto quotation;
    try {
      quotation = exchangeClient.findLatestQuotation(stockSymbol);
    } catch (QuotationNotFoundException e) {
      throw new ValuationFailedException("Valuation failed: no quotation for stock " + stockSymbol, e);
    }
    if (quotation.timestamp().isBefore(now.minus(quotationMaxAge))) {
      throw new ValuationFailedException("Valuation failed: the quotation of stock " + stockSymbol + " is too old");
    }
    return quotation;
  }
}
