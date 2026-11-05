package be.sgerard.demo.employee.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

/**
 *
 * Configuration for user-service integration including retry logic and URL resolution.
 *
 */
@Configuration
@EnableRetry
public class UserServiceConfiguration {

  /**
   *
   * Constructs the base URL for the user-service based on configuration.
   * If BANK_BASE_DOMAIN is set, uses http://bank-user-service.${BANK_BASE_DOMAIN}.
   * Otherwise falls back to BANK_USER_SERVICE_BASE_URL.
   *
   * @param baseDomain the BANK_BASE_DOMAIN environment variable (optional)
   * @param baseUrl the BANK_USER_SERVICE_BASE_URL environment variable (fallback)
   * @param port the optional BANK_USER_SERVICE_PORT
   * @return the resolved base URL for user-service
   *
   */
  @Bean
  public String userServiceBaseUrl(
      @Value("${bank.base-domain:}") String baseDomain,
      @Value("${bank.user-service.base-url:}") String baseUrl,
      @Value("${bank.user-service.port:}") String port) {

    if (baseDomain != null && !baseDomain.isBlank()) {
      final String url = "http://bank-user-service." + baseDomain;
      if (port != null && !port.isBlank()) {
        return url + ":" + port;
      }
      return url;
    }

    if (baseUrl != null && !baseUrl.isBlank()) {
      return baseUrl;
    }

    throw new IllegalStateException(
        "User service base URL not configured. Set either BANK_BASE_DOMAIN or BANK_USER_SERVICE_BASE_URL");
  }

  /**
   *
   * Creates a RestTemplate bean for calling the user-service.
   *
   * @param builder the RestTemplateBuilder
   * @return a configured RestTemplate
   *
   */
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }
}
