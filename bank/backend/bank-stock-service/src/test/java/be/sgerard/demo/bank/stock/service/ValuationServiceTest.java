package be.sgerard.demo.bank.stock.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.sgerard.demo.bank.stock.client.ExchangeServiceClient;
import be.sgerard.demo.bank.stock.client.PortfolioServiceClient;
import be.sgerard.demo.bank.stock.exception.DependencyUnavailableException;
import be.sgerard.demo.bank.stock.exception.PortfolioNotFoundException;
import be.sgerard.demo.bank.stock.exception.QuotationNotFoundException;
import be.sgerard.demo.bank.stock.exception.ValuationFailedException;
import be.sgerard.demo.bank.stock.mapper.PortfolioValuationMapper;
import be.sgerard.demo.bank.stock.model.dto.PortfolioValuationDto;
import be.sgerard.demo.bank.stock.model.dto.QuotationDto;
import be.sgerard.demo.bank.stock.model.dto.RemotePortfolioDto;
import be.sgerard.demo.bank.stock.model.dto.RemotePositionDto;
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
   * Mock portfolio client.
   *
   */
  private PortfolioServiceClient portfolioClient;

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
    portfolioClient = mock(PortfolioServiceClient.class);
    exchangeClient = mock(ExchangeServiceClient.class);
    service = new ValuationService(portfolioClient, exchangeClient, new PortfolioValuationMapper(), CLOCK,
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
  private static RemotePortfolioDto portfolio(UUID id, RemotePositionDto... positions) {
    return new RemotePortfolioDto(id, List.of(positions));
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
    when(portfolioClient.findPortfolio(id)).thenReturn(portfolio(id,
        new RemotePositionDto(UUID.randomUUID(), "APPLE", new BigDecimal("10")),
        new RemotePositionDto(UUID.randomUUID(), "MICROSOFT", new BigDecimal("0.00005")),
        new RemotePositionDto(UUID.randomUUID(), "APPLE", new BigDecimal("2"))));
    when(exchangeClient.findLatestQuotation("APPLE")).thenReturn(quotation("APPLE", "150.5", recent));
    when(exchangeClient.findLatestQuotation("MICROSOFT")).thenReturn(quotation("MICROSOFT", "1", recent));

    final PortfolioValuationDto result = service.valuate(id);

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
    when(portfolioClient.findPortfolio(id)).thenReturn(portfolio(id));

    final PortfolioValuationDto result = service.valuate(id);

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
    when(portfolioClient.findPortfolio(id))
        .thenReturn(portfolio(id, new RemotePositionDto(UUID.randomUUID(), "APPLE", BigDecimal.ONE)));
    when(exchangeClient.findLatestQuotation("APPLE")).thenThrow(new QuotationNotFoundException("none"));

    assertThatThrownBy(() -> service.valuate(id)).isInstanceOf(ValuationFailedException.class)
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
    when(portfolioClient.findPortfolio(id))
        .thenReturn(portfolio(id, new RemotePositionDto(UUID.randomUUID(), "APPLE", BigDecimal.ONE)));

    when(exchangeClient.findLatestQuotation("APPLE"))
        .thenReturn(quotation("APPLE", "10", LocalDateTime.now(CLOCK).minusHours(24)));
    assertThat(service.valuate(id).totalValue()).isEqualByComparingTo("10");

    when(exchangeClient.findLatestQuotation("APPLE"))
        .thenReturn(quotation("APPLE", "10", LocalDateTime.now(CLOCK).minusHours(24).minusSeconds(1)));
    assertThatThrownBy(() -> service.valuate(id)).isInstanceOf(ValuationFailedException.class)
        .hasMessageContaining("too old");
  }

  /**
   *
   * Portfolio lookup and dependency failures are propagated unchanged.
   *
   */
  @Test
  void propagatesPortfolioAndDependencyFailures() {
    final UUID unknown = UUID.randomUUID();
    when(portfolioClient.findPortfolio(unknown)).thenThrow(new PortfolioNotFoundException("none"));
    assertThatThrownBy(() -> service.valuate(unknown)).isInstanceOf(PortfolioNotFoundException.class);

    final UUID id = UUID.randomUUID();
    when(portfolioClient.findPortfolio(id))
        .thenReturn(portfolio(id, new RemotePositionDto(UUID.randomUUID(), "APPLE", BigDecimal.ONE)));
    when(exchangeClient.findLatestQuotation("APPLE")).thenThrow(new DependencyUnavailableException("down"));
    assertThatThrownBy(() -> service.valuate(id)).isInstanceOf(DependencyUnavailableException.class);
  }
}
