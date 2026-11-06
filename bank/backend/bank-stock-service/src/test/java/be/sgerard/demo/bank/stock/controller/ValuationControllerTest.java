package be.sgerard.demo.bank.stock.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.sgerard.demo.bank.stock.client.ExchangeServiceClient;
import be.sgerard.demo.bank.stock.client.PortfolioServiceClient;
import be.sgerard.demo.bank.stock.exception.DependencyUnavailableException;
import be.sgerard.demo.bank.stock.exception.PortfolioNotFoundException;
import be.sgerard.demo.bank.stock.exception.QuotationNotFoundException;
import be.sgerard.demo.bank.stock.model.dto.QuotationDto;
import be.sgerard.demo.bank.stock.model.dto.RemotePortfolioDto;
import be.sgerard.demo.bank.stock.model.dto.RemotePositionDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 *
 * Verifies that the valuation endpoints are secured by permissions, validated and map failures to the
 * expected HTTP statuses.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ValuationControllerTest {

  /**
   *
   * Mock MVC entry point.
   *
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   *
   * Mock portfolio client.
   *
   */
  @MockBean
  private PortfolioServiceClient portfolioClient;

  /**
   *
   * Mock exchange client.
   *
   */
  @MockBean
  private ExchangeServiceClient exchangeClient;

  /**
   *
   * Builds a JWT request post processor holding the given authority.
   *
   * @param authority the granted authority
   * @return the post processor
   *
   */
  private static RequestPostProcessor authority(String authority) {
    return jwt().authorities(new SimpleGrantedAuthority(authority));
  }

  /**
   *
   * Anonymous callers are rejected on every endpoint.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void anonymousIsUnauthorized() throws Exception {
    mockMvc.perform(get("/portfolios/" + UUID.randomUUID() + "/value")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/quotations/AAPL")).andExpect(status().isUnauthorized());
  }

  /**
   *
   * Callers without the matching permission are forbidden.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void withoutMatchingPermissionIsForbidden() throws Exception {
    mockMvc.perform(get("/portfolios/" + UUID.randomUUID() + "/value").with(authority("QUOTATION_READ")))
        .andExpect(status().isForbidden());
    mockMvc.perform(get("/quotations/AAPL").with(authority("PORTFOLIO_VALUATION_READ")))
        .andExpect(status().isForbidden());
  }

  /**
   *
   * A permitted caller gets the valuation of a portfolio.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void returnsValuation() throws Exception {
    final UUID id = UUID.randomUUID();
    when(portfolioClient.findPortfolio(id)).thenReturn(new RemotePortfolioDto(id,
        List.of(new RemotePositionDto(UUID.randomUUID(), "AAPL", new BigDecimal("2")))));
    when(exchangeClient.findLatestQuotation("AAPL")).thenReturn(quotation());

    mockMvc.perform(get("/portfolios/" + id + "/value").with(authority("PORTFOLIO_VALUATION_READ")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.portfolioId").value(id.toString()))
        .andExpect(jsonPath("$.currency").value("EUR"))
        .andExpect(jsonPath("$.totalValue").value(300.0))
        .andExpect(jsonPath("$.positions[0].stockSymbol").value("AAPL"));
  }

  /**
   *
   * Failures are mapped to 404, 422 and 500.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void mapsFailures() throws Exception {
    final UUID unknown = UUID.randomUUID();
    when(portfolioClient.findPortfolio(unknown)).thenThrow(new PortfolioNotFoundException("none"));
    mockMvc.perform(get("/portfolios/" + unknown + "/value").with(authority("PORTFOLIO_VALUATION_READ")))
        .andExpect(status().isNotFound());

    final UUID id = UUID.randomUUID();
    when(portfolioClient.findPortfolio(id)).thenReturn(new RemotePortfolioDto(id,
        List.of(new RemotePositionDto(UUID.randomUUID(), "AAPL", BigDecimal.ONE))));
    when(exchangeClient.findLatestQuotation("AAPL")).thenThrow(new QuotationNotFoundException("none"));
    mockMvc.perform(get("/portfolios/" + id + "/value").with(authority("PORTFOLIO_VALUATION_READ")))
        .andExpect(status().isUnprocessableEntity());

    doThrow(new DependencyUnavailableException("down")).when(exchangeClient).findLatestQuotation("AAPL");
    mockMvc.perform(get("/portfolios/" + id + "/value").with(authority("PORTFOLIO_VALUATION_READ")))
        .andExpect(status().isInternalServerError());
  }

  /**
   *
   * The quotation endpoint returns the quotation, 404 when unknown and 400 for an invalid symbol.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void servesQuotations() throws Exception {
    when(exchangeClient.findLatestQuotation("AAPL")).thenReturn(quotation());
    when(exchangeClient.findLatestQuotation("NONE")).thenThrow(new QuotationNotFoundException("none"));

    mockMvc.perform(get("/quotations/AAPL").with(authority("QUOTATION_READ"))).andExpect(status().isOk())
        .andExpect(jsonPath("$.stockSymbol").value("AAPL"))
        .andExpect(jsonPath("$.volume").value(1000));
    mockMvc.perform(get("/quotations/NONE").with(authority("QUOTATION_READ"))).andExpect(status().isNotFound());
    mockMvc.perform(get("/quotations/BAD$SYMBOL").with(authority("QUOTATION_READ")))
        .andExpect(status().isBadRequest());
  }

  /**
   *
   * Builds the AAPL quotation used by the tests.
   *
   * @return the quotation
   *
   */
  private static QuotationDto quotation() {
    final BigDecimal price = new BigDecimal("150");
    return new QuotationDto("AAPL", price, LocalDateTime.now(), "DAY", price, price, price, price, 1000L);
  }
}
