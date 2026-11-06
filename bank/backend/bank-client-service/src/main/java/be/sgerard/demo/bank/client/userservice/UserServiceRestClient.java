package be.sgerard.demo.bank.client.userservice;

import be.sgerard.demo.bank.client.model.dto.UserServiceCreationRequestDto;
import be.sgerard.demo.bank.client.model.dto.UserServiceCreationResponseDto;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *
 * REST client of the user-service. The JWT of the current request is propagated so that the user
 * creation is performed (and audited) on behalf of the caller.
 *
 */
@Component
public class UserServiceRestClient implements UserServiceClient {

  /**
   *
   * Logger.
   *
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceRestClient.class);

  /**
   *
   * Path of the user creation endpoint, relative to the user-service base URL.
   *
   */
  private static final String USERS_PATH = "/users";

  /**
   *
   * RestTemplate rooted at the user-service base URL.
   *
   */
  private final RestTemplate restTemplate;

  /**
   *
   * Retry policy applied to the user creation call.
   *
   */
  private final RetryTemplate retryTemplate;

  /**
   *
   * Creates a new instance.
   *
   * @param restTemplate the RestTemplate rooted at the user-service base URL
   * @param retryTemplate the retry template of the user-service calls
   *
   */
  public UserServiceRestClient(@Qualifier("userServiceRestTemplate") RestTemplate restTemplate,
                               @Qualifier("userServiceRetryTemplate") RetryTemplate retryTemplate) {
    this.restTemplate = restTemplate;
    this.retryTemplate = retryTemplate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserServiceCreationResponseDto createUser(UserServiceCreationRequestDto request) {
    final HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(currentBearerToken());
    final HttpEntity<UserServiceCreationRequestDto> entity = new HttpEntity<>(request, headers);

    return retryTemplate.execute(context -> {
      if (context.getRetryCount() > 0) {
        LOGGER.warn("Retrying user creation (attempt {}) after: {}", context.getRetryCount() + 1,
            context.getLastThrowable().toString());
      }
      return restTemplate.postForObject(USERS_PATH, entity, UserServiceCreationResponseDto.class);
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteUser(UUID userId) {
    final HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(currentBearerToken());
    final HttpEntity<Void> entity = new HttpEntity<>(headers);

    retryTemplate.execute(context -> {
      restTemplate.exchange(USERS_PATH + "/{id}", HttpMethod.DELETE, entity, Void.class, userId);
      return null;
    });
  }

  /**
   *
   * Extracts the bearer token of the authenticated caller of the current request.
   *
   * @return the raw JWT value
   * @throws IllegalStateException if the current caller is not authenticated with a JWT
   *
   */
  private String currentBearerToken() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
      return jwtAuthentication.getToken().getTokenValue();
    }
    throw new IllegalStateException("Cannot call the user-service: no JWT in the security context");
  }
}
