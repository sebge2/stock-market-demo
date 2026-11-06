package be.sgerard.demo.bank.stock.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import be.sgerard.demo.bank.stock.config.RemoteServicesConfiguration;
import be.sgerard.demo.bank.stock.exception.DependencyUnavailableException;
import be.sgerard.demo.bank.stock.exception.PortfolioNotFoundException;
import be.sgerard.demo.bank.stock.exception.QuotationNotFoundException;
import be.sgerard.demo.bank.stock.model.dto.QuotationDto;
import be.sgerard.demo.bank.stock.model.dto.RemotePortfolioDto;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

/**
 *
 * Tests of the REST clients: token propagation, error mapping, retries and URL resolution.
 *
 */
class RemoteClientsTest {

  /**
   *
   * Root URL used by the mocked servers.
   *
   */
  private static final String ROOT = "http://remote";

  /**
   *
   * Retry template of the production configuration.
   *
   */
  private final RetryTemplate retryTemplate = new RemoteServicesConfiguration().remoteRetryTemplate();

  /**
   *
   * RestTemplate shared by the clients under test.
   *
   */
  private RestTemplate restTemplate;

  /**
   *
   * Mock server bound to the RestTemplate.
   *
   */
  private MockRestServiceServer server;

  /**
   *
   * Authenticates a caller carrying a JWT.
   *
   */
  @BeforeEach
  void setUp() {
    restTemplate = new RestTemplate();
    restTemplate.setUriTemplateHandler(new org.springframework.web.util.DefaultUriBuilderFactory(ROOT));
    server = MockRestServiceServer.bindTo(restTemplate).build();
    final Jwt jwt = new Jwt("caller-token", Instant.now(), Instant.now().plusSeconds(60),
        java.util.Map.of("alg", "none"), java.util.Map.of("sub", "user"));
    SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
  }

  /**
   *
   * Clears the security context.
   *
   */
  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  /**
   *
   * The portfolio is fetched with the caller's token.
   *
   */
  @Test
  void fetchesPortfolioWithCallerToken() {
    final UUID id = UUID.randomUUID();
    server.expect(requestTo(ROOT + "/portfolios/" + id))
        .andExpect(header("Authorization", "Bearer caller-token"))
        .andRespond(withSuccess("{\"id\":\"" + id + "\",\"positions\":[],\"clientId\":\"" + UUID.randomUUID()
            + "\"}", MediaType.APPLICATION_JSON));

    final RemotePortfolioDto portfolio = new PortfolioServiceClientImpl(restTemplate, retryTemplate)
        .findPortfolio(id);

    assertThat(portfolio.id()).isEqualTo(id);
    server.verify();
  }

  /**
   *
   * A 404 from the portfolio service becomes a portfolio-not-found failure, without retry.
   *
   */
  @Test
  void mapsPortfolioNotFound() {
    final UUID id = UUID.randomUUID();
    server.expect(requestTo(ROOT + "/portfolios/" + id)).andRespond(withResourceNotFound());

    assertThatThrownBy(() -> new PortfolioServiceClientImpl(restTemplate, retryTemplate).findPortfolio(id)).isInstanceOf(PortfolioNotFoundException.class);
  }

  /**
   *
   * A quotation is fetched with an encoded symbol and the caller's token.
   *
   */
  @Test
  void fetchesQuotation() {
    server.expect(requestTo(ROOT + "/listings/AAPL/latest-price")).andExpect(header("Authorization",
            "Bearer caller-token"))
        .andRespond(withSuccess("{\"stockSymbol\":\"AAPL\",\"currentPrice\":150.5,"
            + "\"timestamp\":\"2026-01-10T10:00:00\",\"volume\":10}", MediaType.APPLICATION_JSON));

    final QuotationDto quotation = new ExchangeServiceClientImpl(restTemplate, retryTemplate)
        .findLatestQuotation("AAPL");

    assertThat(quotation.currentPrice()).isEqualByComparingTo("150.5");
    server.verify();
  }

  /**
   *
   * A 404 from the exchange becomes a quotation-not-found failure.
   *
   */
  @Test
  void mapsQuotationNotFound() {
    server.expect(requestTo(ROOT + "/listings/NONE/latest-price")).andRespond(withResourceNotFound());

    assertThatThrownBy(() -> new ExchangeServiceClientImpl(restTemplate, retryTemplate)
        .findLatestQuotation("NONE")).isInstanceOf(QuotationNotFoundException.class);
  }

  /**
   *
   * 5xx answers are retried up to three attempts, then reported as a dependency failure; a later success
   * is returned.
   *
   */
  @Test
  void retriesServerErrors() {
    server.expect(requestTo(ROOT + "/listings/AAPL/latest-price")).andRespond(withServerError());
    server.expect(requestTo(ROOT + "/listings/AAPL/latest-price")).andRespond(withServerError());
    server.expect(requestTo(ROOT + "/listings/AAPL/latest-price"))
        .andRespond(withSuccess("{\"stockSymbol\":\"AAPL\",\"currentPrice\":1,"
            + "\"timestamp\":\"2026-01-10T10:00:00\",\"volume\":1}", MediaType.APPLICATION_JSON));
    assertThat(new ExchangeServiceClientImpl(restTemplate, retryTemplate).findLatestQuotation("AAPL"))
        .isNotNull();
    server.verify();

    server.reset();
    for (int i = 0; i < 3; i++) {
      server.expect(requestTo(ROOT + "/listings/AAPL/latest-price")).andExpect(
          org.springframework.test.web.client.match.MockRestRequestMatchers.method(HttpMethod.GET))
          .andRespond(withServerError());
    }
    assertThatThrownBy(() -> new ExchangeServiceClientImpl(restTemplate, retryTemplate)
        .findLatestQuotation("AAPL")).isInstanceOf(DependencyUnavailableException.class);
    server.verify();
  }

  /**
   *
   * Without JWT in the security context no remote call is made.
   *
   */
  @Test
  void requiresJwt() {
    SecurityContextHolder.clearContext();

    assertThatThrownBy(() -> new ExchangeServiceClientImpl(restTemplate, retryTemplate)
        .findLatestQuotation("AAPL")).isInstanceOf(IllegalStateException.class);
  }

  /**
   *
   * The base URL is derived from the base domain when set, otherwise the explicit URL is used.
   *
   */
  @Test
  void resolvesBaseUrl() {
    assertThat(RemoteServicesConfiguration.resolveBaseUrl("svc", "/api/x", "demo.test", "8080", "http://other"))
        .isEqualTo("http://svc.demo.test:8080/api/x");
    assertThat(RemoteServicesConfiguration.resolveBaseUrl("svc", "/api/x", "", "", "http://other/api/"))
        .isEqualTo("http://other/api");
    assertThatThrownBy(() -> RemoteServicesConfiguration.resolveBaseUrl("svc", "/api/x", "", "", ""))
        .isInstanceOf(IllegalStateException.class);
  }
}
