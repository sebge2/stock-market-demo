package be.sgerard.demo.bank.stock.client;

import be.sgerard.demo.bank.stock.exception.DependencyUnavailableException;
import be.sgerard.demo.bank.stock.exception.PortfolioNotFoundException;
import be.sgerard.demo.bank.stock.model.dto.RemotePortfolioDto;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * REST implementation of the portfolio service client. The JWT of the current request is propagated.
 *
 */
@Component
public class PortfolioServiceClientImpl implements PortfolioServiceClient {

  /**
   *
   * RestTemplate rooted at the portfolio service base URL.
   *
   */
  private final RestTemplate restTemplate;

  /**
   *
   * Retry policy applied to the calls.
   *
   */
  private final RetryTemplate retryTemplate;

  /**
   *
   * Creates a new instance.
   *
   * @param restTemplate the RestTemplate rooted at the portfolio service base URL
   * @param retryTemplate the retry template of the remote calls
   *
   */
  public PortfolioServiceClientImpl(@Qualifier("portfolioServiceRestTemplate") RestTemplate restTemplate,
      @Qualifier("remoteRetryTemplate") RetryTemplate retryTemplate) {
    this.restTemplate = restTemplate;
    this.retryTemplate = retryTemplate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RemotePortfolioDto findPortfolio(UUID portfolioId) {
    final HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(CallerToken.current());
    final HttpEntity<Void> entity = new HttpEntity<>(headers);

    try {
      final RemotePortfolioDto portfolio = retryTemplate.execute(context -> restTemplate
          .exchange("/portfolios/{id}", HttpMethod.GET, entity, RemotePortfolioDto.class, portfolioId).getBody());
      if (portfolio == null) {
        throw new DependencyUnavailableException("The portfolio service returned an empty answer");
      }
      return portfolio;
    } catch (HttpClientErrorException.NotFound e) {
      throw new PortfolioNotFoundException("Portfolio " + portfolioId + " not found");
    } catch (RestClientException e) {
      throw new DependencyUnavailableException("The portfolio service call failed", e);
    }
  }
}
