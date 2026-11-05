package be.sgerard.demo.employee.client;

import be.sgerard.demo.employee.model.dto.UserServiceCreationRequestDto;
import be.sgerard.demo.employee.model.dto.UserServiceCreationResponseDto;
import java.util.UUID;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

/**
 *
 * Interface for communicating with the user-service to create users.
 *
 */
public interface IUserServiceClient {

  /**
   *
   * Creates a user in the user-service on behalf of the current caller (the bearer token of the
   * current request is forwarded). Transient failures (5xx, I/O errors, timeouts) are retried, 4xx
   * responses are not.
   *
   * @param request the user creation request
   * @return the created user response
   * @throws HttpClientErrorException if the user-service rejected the request (4xx)
   * @throws HttpServerErrorException if the user-service kept failing (5xx) after all attempts
   * @throws ResourceAccessException if the user-service stayed unreachable after all attempts
   *
   */
  UserServiceCreationResponseDto createUser(UserServiceCreationRequestDto request);

  /**
   *
   * Deletes a user of the user-service on behalf of the current caller. Used to compensate a user
   * creation when the employee cannot be persisted afterwards.
   *
   * @param userId the identifier of the user to delete
   *
   */
  void deleteUser(UUID userId);
}
