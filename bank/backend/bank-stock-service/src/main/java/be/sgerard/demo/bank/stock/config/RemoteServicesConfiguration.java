package be.sgerard.demo.bank.stock.config;

import java.time.Clock;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * Configuration of the integration with the exchange company service: URL
 * resolution, HTTP timeouts and retry policy.
 *
 */
@Configuration
public class RemoteServicesConfiguration {

  /**
   *
   * Maximum number of attempts (initial call included) of a remote call.
   *
   */
  public static final int MAX_ATTEMPTS = 3;

  /**
   *
   * Delay in milliseconds before the first retry; it doubles on every further retry (with jitter).
   *
   */
  public static final long INITIAL_BACKOFF_MS = 100;

  /**
   *
   * Upper bound in milliseconds of the delay between two attempts.
   *
   */
  public static final long MAX_BACKOFF_MS = 1_000;

  /**
   *
   * Context path of the exchange company service, used when its URL is derived from the base domain.
   *
   */
  public static final String COMPANY_CONTEXT_PATH = "/api/company";

  /**
   *
   * Timeout applied when connecting to and reading from a remote service.
   *
   */
  private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(5);

  /**
   *
   * Resolves the base URL of a remote service. If the base domain is set the URL is
   * {@code http://<serviceName>.<domain>[:<port>]<contextPath>}, otherwise the explicit base URL is used.
   *
   * @param serviceName host name of the service inside the base domain
   * @param contextPath context path of the service, only used with the base domain
   * @param baseDomain the base domain of the services (optional)
   * @param port the port of the service, only used with the base domain (optional)
   * @param baseUrl the explicit base URL of the service (fallback)
   * @return the resolved base URL, without trailing slash
   * @throws IllegalStateException if neither the base domain nor the base URL is configured
   *
   */
  public static String resolveBaseUrl(String serviceName, String contextPath, String baseDomain, String port,
      String baseUrl) {
    final String url;
    if (baseDomain != null && !baseDomain.isBlank()) {
      final String hostUrl = "http://" + serviceName + "." + baseDomain.trim();
      url = (port != null && !port.isBlank() ? hostUrl + ":" + port.trim() : hostUrl) + contextPath;
    } else if (baseUrl != null && !baseUrl.isBlank()) {
      url = baseUrl.trim();
    } else {
      throw new IllegalStateException("URL of " + serviceName + " not configured: set BANK_BASE_DOMAIN or its base URL");
    }
    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
  }

  /**
   *
   * Creates the RestTemplate dedicated to the exchange company service.
   *
   * @param builder the RestTemplateBuilder
   * @param baseDomain the base domain of the services (optional)
   * @param port the port of the exchange company service (optional)
   * @param baseUrl the explicit base URL of the exchange company service (fallback)
   * @return the configured RestTemplate
   *
   */
  @Bean
  @Qualifier("exchangeServiceRestTemplate")
  public RestTemplate exchangeServiceRestTemplate(RestTemplateBuilder builder,
      @Value("${bank.base-domain:}") String baseDomain,
      @Value("${bank.exchange-company-service.port:}") String port,
      @Value("${bank.exchange-company-service.base-url:}") String baseUrl) {
    return builder
        .rootUri(resolveBaseUrl("exchange-company-service", COMPANY_CONTEXT_PATH, baseDomain, port, baseUrl))
        .setConnectTimeout(HTTP_TIMEOUT)
        .setReadTimeout(HTTP_TIMEOUT)
        .build();
  }

  /**
   *
   * Creates the retry template of the remote calls: {@value #MAX_ATTEMPTS} attempts, exponential backoff
   * (100ms, 200ms, ...) with jitter, retrying only 5xx responses and I/O errors or timeouts. 4xx responses
   * are never retried.
   *
   * @return the retry template
   *
   */
  @Bean
  @Qualifier("remoteRetryTemplate")
  public RetryTemplate remoteRetryTemplate() {
    return RetryTemplate.builder()
        .maxAttempts(MAX_ATTEMPTS)
        .exponentialBackoff(INITIAL_BACKOFF_MS, 2.0, MAX_BACKOFF_MS, true)
        .retryOn(HttpServerErrorException.class)
        .retryOn(ResourceAccessException.class)
        .build();
  }

  /**
   *
   * Provides the clock used to timestamp valuations and to detect stale quotations.
   *
   * @return the system clock
   *
   */
  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }
}
