package be.sgerard.demo.employee.client;

import be.sgerard.demo.employee.model.dto.UserServiceCreationRequestDto;
import be.sgerard.demo.employee.model.dto.UserServiceCreationResponseDto;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * Client for communicating with the user-service to create users.
 * Includes retry logic for transient failures.
 *
 */
@Component
public class UserServiceClient implements IUserServiceClient {

  /**
   *
   * Logger.
   *
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceClient.class);

  /**
   *
   * User-service base URL.
   *
   */
  private final String userServiceBaseUrl;

  /**
   *
   * RestTemplate for making HTTP calls.
   *
   */
  private final RestTemplate restTemplate;

  /**
   *
   * Creates a new instance.
   *
   * @param userServiceBaseUrl the user-service base URL
   * @param restTemplate the RestTemplate for making HTTP calls
   *
   */
  public UserServiceClient(String userServiceBaseUrl, RestTemplate restTemplate) {
    this.userServiceBaseUrl = userServiceBaseUrl;
    this.restTemplate = restTemplate;
  }

  /**
   *
   * Creates a user in the user-service with retry logic.
   * Retries on 5xx errors, timeouts, and connection failures.
   * Does not retry on 4xx client errors.
   *
   * @param request the user creation request
   * @return the created user response
   * @throws HttpClientErrorException if a 4xx error occurs
   * @throws Exception if the request fails after retries
   *
   */
  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2.0, random = true),
      recover = "handleUserCreationFailure"
  )
  public UserServiceCreationResponseDto createUser(UserServiceCreationRequestDto request) {
    final String url = userServiceBaseUrl + "/users";

    try {
      LOGGER.debug("Attempting to create user in user-service at {}", url);
      UserServiceCreationResponseDto response = restTemplate.postForObject(
          url,
          request,
          UserServiceCreationResponseDto.class
      );
      LOGGER.info("Successfully created user in user-service with ID: {}", response.getId());
      return response;
    } catch (HttpClientErrorException e) {
      // Do not retry on 4xx errors
      LOGGER.warn("Client error when creating user (status: {}): {}", e.getStatusCode(),
          e.getMessage());
      throw e;
    } catch (Exception e) {
      // Log and rethrow for retry handling
      LOGGER.warn("Transient error when creating user: {}. Will retry.", e.getMessage());
      throw new RuntimeException("Failed to create user in user-service", e);
    }
  }
}
