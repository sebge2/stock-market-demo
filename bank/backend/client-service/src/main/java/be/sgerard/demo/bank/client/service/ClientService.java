package be.sgerard.demo.bank.client.service;

import be.sgerard.demo.bank.client.mapper.ClientMapper;
import be.sgerard.demo.bank.client.model.Client;
import be.sgerard.demo.bank.client.model.dto.ClientDto;
import be.sgerard.demo.bank.client.model.dto.ClientRequestDto;
import be.sgerard.demo.bank.client.model.dto.UserServiceCreationRequestDto;
import be.sgerard.demo.bank.client.model.dto.UserServiceCreationResponseDto;
import be.sgerard.demo.bank.client.repository.ClientRepository;
import be.sgerard.demo.bank.client.userservice.UserServiceClient;
import be.sgerard.demo.bank.client.util.UserCreationHelper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Provides CRUD operations for clients.
 *
 */
@Service
@Transactional(readOnly = true)
public class ClientService {

  /**
   *
   * Logger.
   *
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientService.class);

  /**
   *
   * Repository for client persistence.
   *
   */
  private final ClientRepository clientRepository;

  /**
   *
   * Mapper for client entity/DTO conversion.
   *
   */
  private final ClientMapper clientMapper;

  /**
   *
   * Client for user-service integration.
   *
   */
  private final UserServiceClient userServiceClient;

  /**
   *
   * Creates the service.
   *
   * @param clientRepository the client repository
   * @param clientMapper the client mapper
   * @param userServiceClient the user-service client
   *
   */
  public ClientService(ClientRepository clientRepository, ClientMapper clientMapper,
      UserServiceClient userServiceClient) {
    this.clientRepository = clientRepository;
    this.clientMapper = clientMapper;
    this.userServiceClient = userServiceClient;
  }

  /**
   *
   * Returns a page of clients.
   *
   * @param pageable the pagination information
   * @return the page of clients
   *
   */
  public Page<ClientDto> findAll(Pageable pageable) {
    return clientRepository.findAll(pageable).map(clientMapper::toDto);
  }

  /**
   *
   * Returns a client by identifier.
   *
   * @param id the client identifier
   * @return the client DTO
   * @throws ResponseStatusException if the client is not found
   *
   */
  public ClientDto findById(UUID id) {
    return clientMapper.toDto(findEntity(id));
  }

  /**
   *
   * Creates a client and the corresponding user in the user-service (auto-generated username and
   * temporary password). The user is created before the client is persisted: if its creation fails
   * nothing is persisted.
   *
   * @param request the client creation request
   * @return the created client DTO
   * @throws ResponseStatusException if the email is missing or the user creation fails
   *
   */
  @Transactional
  public ClientDto create(ClientRequestDto request) {
    if (request.getEmail() == null || request.getEmail().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The email is required to create the client user");
    }
    LOGGER.info("Creating user in user-service for a new client");
    final UUID userId = createUserInUserService(request);

    final ClientDto result;
    try {
      result = clientMapper.toDto(clientRepository.saveAndFlush(clientMapper.toEntity(request, userId)));
    } catch (RuntimeException e) {
      compensateUserCreation(userId);
      throw e;
    }
    LOGGER.info("Client created successfully with ID: {}", result.getId());
    return result;
  }

  /**
   *
   * Updates a client. The email is not stored by the client service and is ignored.
   *
   * @param id the client identifier
   * @param request the client update request
   * @return the updated client DTO
   * @throws ResponseStatusException if the client is not found
   *
   */
  @Transactional
  public ClientDto update(UUID id, ClientRequestDto request) {
    final Client client = findEntity(id);
    clientMapper.updateEntity(request, client);
    return clientMapper.toDto(clientRepository.saveAndFlush(client));
  }

  /**
   *
   * Deletes a client.
   *
   * @param id the client identifier
   * @throws ResponseStatusException if the client is not found
   *
   */
  @Transactional
  public void delete(UUID id) {
    clientRepository.delete(findEntity(id));
  }

  /**
   *
   * Best-effort compensation: deletes the user created in the user-service because the client
   * could not be persisted. A failure is only logged so that the original error is not masked.
   * Failures raised at commit time (after this method returns) are not compensated.
   *
   * @param userId the identifier of the user to delete
   *
   */
  private void compensateUserCreation(UUID userId) {
    try {
      userServiceClient.deleteUser(userId);
      LOGGER.warn("Client persistence failed, user {} deleted from the user-service", userId);
    } catch (RuntimeException e) {
      LOGGER.error("Client persistence failed and user {} could not be deleted (orphan user)",
          userId, e);
    }
  }

  /**
   *
   * Creates a user in the user-service with auto-generated credentials and translates the failures
   * to ResponseStatusExceptions: 409 for a username conflict, 400 for a rejected request, 502 for an
   * unexpected upstream answer, 503 when the user-service is unreachable and 500 otherwise.
   *
   * @param request the client creation request
   * @return the identifier of the created user
   * @throws ResponseStatusException if the user cannot be created
   *
   */
  private UUID createUserInUserService(ClientRequestDto request) {
    final String username = UserCreationHelper.generateUsername(request.getEmail());
    final UserServiceCreationRequestDto userRequest = new UserServiceCreationRequestDto(
        username,
        request.getFirstName() + " " + request.getLastName(),
        request.getEmail(),
        UserCreationHelper.generateTemporaryPassword(),
        UserCreationHelper.createDefaultRoleIds());

    final UserServiceCreationResponseDto response;
    try {
      response = userServiceClient.createUser(userRequest);
    } catch (HttpClientErrorException e) {
      LOGGER.warn("User-service rejected the user creation: {} - {}", e.getStatusCode(),
          e.getResponseBodyAsString());
      if (e.getStatusCode() == HttpStatus.CONFLICT) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
            "Failed to create user: username already exists: " + username);
      }
      if (e.getStatusCode() == HttpStatus.BAD_REQUEST
          || e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Failed to create user: the user-service rejected the request");
      }
      // 401/403/...: the caller cannot create users, which is not a client mistake of this request.
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
          "Failed to create user: the user-service refused the request (" + e.getStatusCode() + ")");
    } catch (HttpServerErrorException e) {
      LOGGER.error("User-service kept failing after retries: {}", e.getStatusCode());
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
          "Failed to create user: the user-service failed. Please retry later.");
    } catch (ResourceAccessException e) {
      LOGGER.error("User-service unreachable after retries: {}", e.getMessage());
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
          "Failed to create user: the user-service is unavailable. Please retry later.");
    } catch (RuntimeException e) {
      LOGGER.error("Unexpected error while creating the user", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Failed to create user: unexpected error");
    }

    if (response == null || response.getId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
          "Failed to create user: the user-service returned no user identifier");
    }
    return response.getId();
  }

  /**
   *
   * Finds a client by ID.
   *
   * @param id the client identifier
   * @return the client entity
   * @throws ResponseStatusException if the client is not found
   *
   */
  private Client findEntity(UUID id) {
    return clientRepository.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found: " + id));
  }
}
