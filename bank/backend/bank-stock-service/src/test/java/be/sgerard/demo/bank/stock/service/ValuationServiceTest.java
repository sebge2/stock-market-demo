package be.sgerard.demo.bank.stock.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.sgerard.demo.bank.stock.client.ExchangeServiceClient;
import be.sgerard.demo.bank.stock.exception.DependencyUnavailableException;
import be.sgerard.demo.bank.stock.exception.QuotationNotFoundException;
import be.sgerard.demo.bank.stock.exception.ValuationFailedException;
import be.sgerard.demo.bank.stock.mapper.PortfolioValuationMapper;
import be.sgerard.demo.bank.stock.model.dto.PortfolioValuationDto;
import be.sgerard.demo.bank.stock.model.dto.QuotationDto;
import be.sgerard.demo.bank.stock.model.external.PortfolioDto;
import be.sgerard.demo.bank.stock.model.external.PositionDto;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * Unit tests of the valuation service with mocked remote clients.
 *
 */
class ValuationServiceTest {

  /**
   *
   * Fixed clock of the tests.
   *
   */
  private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-01-10T12:00:00Z"), ZoneOffset.UTC);

  /**
   *
   * Mock exchange client.
   *
   */
  private ExchangeServiceClient exchangeClient;

  /**
   *
   * Service under test.
   *
   */
  private ValuationService service;

  /**
   *
   * Creates the service under test.
   *
   */
  @BeforeEach
  void setUp() {
    exchangeClient = mock(ExchangeServiceClient.class);
    service = new ValuationService(exchangeClient, new PortfolioValuationMapper(), CLOCK,
        Duration.ofHours(24));
  }

  /**
   *
   * Builds a quotation of the given price and timestamp.
   *
   * @param symbol stock symbol
   * @param price current price
   * @param timestamp quotation instant
   * @return the quotation
   *
   */
  private static QuotationDto quotation(String symbol, String price, LocalDateTime timestamp) {
    final BigDecimal value = new BigDecimal(price);
    return new QuotationDto(symbol, value, timestamp, "DAY", value, value, value, value, 100L);
  }

  /**
   *
   * Builds a portfolio with the given positions.
   *
   * @param id portfolio identifier
   * @param positions the positions
   * @return the portfolio
   *
   */
  private static PortfolioDto portfolio(UUID id, PositionDto... positions) {
    return new PortfolioDto(id, List.of(positions));
  }

  /**
   *
   * The total is the sum of quantity times price, rounded half-even at scale 4.
   *
   */
  @Test
  void valuatesPortfolioWithRounding() {
    final UUID id = UUID.randomUUID();
    final LocalDateTime recent = LocalDateTime.now(CLOCK).minusMinutes(5);
    final PortfolioDto portfolio = portfolio(id,
        new PositionDto(UUID.randomUUID(), "APPLE", new BigDecimal("10")),
        new PositionDto(UUID.randomUUID(), "MICROSOFT", new BigDecimal("0.00005")),
        new PositionDto(UUID.randomUUID(), "APPLE", new BigDecimal("2")));
    when(exchangeClient.findLatestQuotation("APPLE")).thenReturn(quotation("APPLE", "150.5", recent));
    when(exchangeClient.findLatestQuotation("MICROSOFT")).thenReturn(quotation("MICROSOFT", "1", recent));

    final PortfolioValuationDto result = service.valuate(portfolio);

    assertThat(result.portfolioId()).isEqualTo(id);
    assertThat(result.currency()).isEqualTo("EUR");
    assertThat(result.valuedAt()).isEqualTo(LocalDateTime.now(CLOCK));
    assertThat(result.positions()).hasSize(3);
    assertThat(result.positions().get(0).positionValue()).isEqualByComparingTo("1505.0000");
    // 0.00005 rounds half-even to 0.0000
    assertThat(result.positions().get(1).positionValue()).isEqualByComparingTo("0.0000");
    assertThat(result.totalValue()).isEqualByComparingTo("1806.0000");
    assertThat(result.positions().get(0).priceTimestamp()).isEqualTo(recent);
  }

  /**
   *
   * A portfolio without position is worth zero.
   *
   */
  @Test
  void valuatesEmptyPortfolioToZero() {
    final UUID id = UUID.randomUUID();
    final PortfolioDto portfolio = portfolio(id);

    final PortfolioValuationDto result = service.valuate(portfolio);

    assertThat(result.totalValue()).isEqualByComparingTo("0");
    assertThat(result.positions()).isEmpty();
  }

  /**
   *
   * A missing quotation fails the valuation.
   *
   */
  @Test
  void failsWhenQuotationIsMissing() {
    final UUID id = UUID.randomUUID();
    final PortfolioDto portfolio = portfolio(id, new PositionDto(UUID.randomUUID(), "APPLE", BigDecimal.ONE));
    when(exchangeClient.findLatestQuotation("APPLE")).thenThrow(new QuotationNotFoundException("none"));

    assertThatThrownBy(() -> service.valuate(portfolio)).isInstanceOf(ValuationFailedException.class)
        .hasMessageContaining("APPLE");
  }

  /**
   *
   * A quotation older than the accepted age fails the valuation, one exactly at the limit is accepted.
   *
   */
  @Test
  void failsWhenQuotationIsStale() {
    final UUID id = UUID.randomUUID();
    final PortfolioDto portfolio = portfolio(id, new PositionDto(UUID.randomUUID(), "APPLE", BigDecimal.ONE));

    when(exchangeClient.findLatestQuotation("APPLE"))
        .thenReturn(quotation("APPLE", "10", LocalDateTime.now(CLOCK).minusHours(24)));
    assertThat(service.valuate(portfolio).totalValue()).isEqualByComparingTo("10");

    when(exchangeClient.findLatestQuotation("APPLE"))
        .thenReturn(quotation("APPLE", "10", LocalDateTime.now(CLOCK).minusHours(24).minusSeconds(1)));
    assertThatThrownBy(() -> service.valuate(portfolio)).isInstanceOf(ValuationFailedException.class)
        .hasMessageContaining("too old");
  }

  /**
   *
   * Dependency failures are propagated unchanged.
   *
   */
  @Test
  void propagatesDependencyFailures() {
    final PortfolioDto portfolio = portfolio(UUID.randomUUID(),
        new PositionDto(UUID.randomUUID(), "APPLE", BigDecimal.ONE));
    when(exchangeClient.findLatestQuotation("APPLE")).thenThrow(new DependencyUnavailableException("down"));
    assertThatThrownBy(() -> service.valuate(portfolio)).isInstanceOf(DependencyUnavailableException.class);
  }
}
