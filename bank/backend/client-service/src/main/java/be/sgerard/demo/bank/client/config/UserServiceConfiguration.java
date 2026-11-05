package be.sgerard.demo.bank.client.config;

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
 * Configuration of the integration with the user service: URL resolution, HTTP timeouts and retry policy.
 *
 */
@Configuration
public class UserServiceConfiguration {

  /**
   *
   * Maximum number of attempts (initial call included) of a call to the user service.
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
   * Timeout applied when connecting to and reading from the user service.
   *
   */
  private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(5);

  /**
   *
   * Resolves the base URL of the user service. If the base domain is set the URL is
   * {@code http://bank-user-service.<domain>[:<port>]}, otherwise the explicit base URL is used.
   *
   * @param baseDomain the base domain of the bank services (optional)
   * @param port the port of the user service, only used with the base domain (optional)
   * @param baseUrl the explicit base URL of the user service (fallback)
   * @return the resolved base URL, without trailing slash
   * @throws IllegalStateException if neither the base domain nor the base URL is configured
   *
   */
  public static String resolveBaseUrl(String baseDomain, String port, String baseUrl) {
    final String url;
    if (baseDomain != null && !baseDomain.isBlank()) {
      final String hostUrl = "http://bank-user-service." + baseDomain.trim();
      url = port != null && !port.isBlank() ? hostUrl + ":" + port.trim() : hostUrl;
    } else if (baseUrl != null && !baseUrl.isBlank()) {
      url = baseUrl.trim();
    } else {
      throw new IllegalStateException(
          "User service URL not configured: set BANK_BASE_DOMAIN or BANK_USER_SERVICE_BASE_URL");
    }
    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
  }

  /**
   *
   * Creates the RestTemplate dedicated to the user service, rooted at its resolved base URL.
   *
   * @param builder the RestTemplateBuilder
   * @param baseDomain the base domain of the bank services (optional)
   * @param port the port of the user service (optional)
   * @param baseUrl the explicit base URL of the user service (fallback)
   * @return the configured RestTemplate
   *
   */
  @Bean
  @Qualifier("userServiceRestTemplate")
  public RestTemplate userServiceRestTemplate(RestTemplateBuilder builder,
      @Value("${bank.base-domain:}") String baseDomain,
      @Value("${bank.user-service.port:}") String port,
      @Value("${bank.user-service.base-url:}") String baseUrl) {
    return builder
        .rootUri(resolveBaseUrl(baseDomain, port, baseUrl))
        .setConnectTimeout(HTTP_TIMEOUT)
        .setReadTimeout(HTTP_TIMEOUT)
        .build();
  }

  /**
   *
   * Creates the retry template of the user service calls: {@value #MAX_ATTEMPTS} attempts, exponential
   * backoff (100ms, 200ms, ...) with jitter, retrying only 5xx responses and I/O errors or timeouts.
   * 4xx responses are never retried.
   *
   * @return the retry template
   *
   */
  @Bean
  @Qualifier("userServiceRetryTemplate")
  public RetryTemplate userServiceRetryTemplate() {
    return RetryTemplate.builder()
        .maxAttempts(MAX_ATTEMPTS)
        .exponentialBackoff(INITIAL_BACKOFF_MS, 2.0, MAX_BACKOFF_MS, true)
        .retryOn(HttpServerErrorException.class)
        .retryOn(ResourceAccessException.class)
        .build();
  }
}
