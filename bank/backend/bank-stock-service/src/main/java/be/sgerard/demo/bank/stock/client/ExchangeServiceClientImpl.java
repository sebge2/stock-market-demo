package be.sgerard.demo.bank.stock.client;

import be.sgerard.demo.bank.stock.exception.DependencyUnavailableException;
import be.sgerard.demo.bank.stock.exception.QuotationNotFoundException;
import be.sgerard.demo.bank.stock.model.dto.QuotationDto;
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
 * REST implementation of the exchange service client. The JWT of the current request is propagated.
 *
 */
@Component
public class ExchangeServiceClientImpl implements ExchangeServiceClient {

  /**
   *
   * Path of the latest price endpoint, relative to the exchange company service base URL.
   *
   */
  private static final String LATEST_PRICE_PATH = "/listings/{symbol}/latest-price";

  /**
   *
   * RestTemplate rooted at the exchange company service base URL.
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
   * @param restTemplate the RestTemplate rooted at the exchange company service base URL
   * @param retryTemplate the retry template of the remote calls
   *
   */
  public ExchangeServiceClientImpl(@Qualifier("exchangeServiceRestTemplate") RestTemplate restTemplate,
      @Qualifier("remoteRetryTemplate") RetryTemplate retryTemplate) {
    this.restTemplate = restTemplate;
    this.retryTemplate = retryTemplate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QuotationDto findLatestQuotation(String stockSymbol) {
    final HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(CallerToken.current());
    final HttpEntity<Void> entity = new HttpEntity<>(headers);

    try {
      final QuotationDto quotation = retryTemplate.execute(context -> restTemplate
          .exchange(LATEST_PRICE_PATH, HttpMethod.GET, entity, QuotationDto.class, stockSymbol).getBody());
      if (quotation == null || quotation.currentPrice() == null || quotation.timestamp() == null) {
        throw new DependencyUnavailableException("The exchange returned an incomplete quotation for " + stockSymbol);
      }
      return quotation;
    } catch (HttpClientErrorException.NotFound e) {
      throw new QuotationNotFoundException("No quotation found for stock " + stockSymbol);
    } catch (RestClientException e) {
      throw new DependencyUnavailableException("The exchange service call failed", e);
    }
  }
}
