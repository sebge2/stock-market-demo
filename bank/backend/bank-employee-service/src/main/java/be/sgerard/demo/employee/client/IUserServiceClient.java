package be.sgerard.demo.employee.client;

import be.sgerard.demo.employee.model.dto.UserServiceCreationRequestDto;
import be.sgerard.demo.employee.model.dto.UserServiceCreationResponseDto;

/**
 *
 * Interface for communicating with the user-service to create users.
 *
 */
public interface IUserServiceClient {

  /**
   *
   * Creates a user in the user-service with retry logic.
   * Retries on 5xx errors, timeouts, and connection failures.
   * Does not retry on 4xx client errors.
   *
   * @param request the user creation request
   * @return the created user response
   * @throws RuntimeException if the request fails
   *

   */
  UserServiceCreationResponseDto createUser(UserServiceCreationRequestDto request);
}
