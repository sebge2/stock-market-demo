package be.sgerard.demo.bank.client.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.sgerard.demo.bank.client.mapper.ClientMapper;
import be.sgerard.demo.bank.client.model.Client;
import be.sgerard.demo.bank.client.model.ClientStatus;
import be.sgerard.demo.bank.client.model.ClientType;
import be.sgerard.demo.bank.client.model.dto.ClientDto;
import be.sgerard.demo.bank.client.model.dto.ClientRequestDto;
import be.sgerard.demo.bank.client.model.dto.UserServiceCreationRequestDto;
import be.sgerard.demo.bank.client.model.dto.UserServiceCreationResponseDto;
import be.sgerard.demo.bank.client.repository.ClientRepository;
import be.sgerard.demo.bank.client.userservice.UserServiceClient;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Unit tests of the client service and its integration with the user-service client.
 *
 */
class ClientServiceTest {

  /**
   *
   * Mock client repository.
   *
   */
  private ClientRepository clientRepository;

  /**
   *
   * Mock user-service client.
   *
   */
  private UserServiceClient userServiceClient;

  /**
   *
   * Service under test.
   *
   */
  private ClientService clientService;

  /**
   *
   * Creates the service under test with mocked collaborators.
   *
   */
  @BeforeEach
  void setUp() {
    clientRepository = mock(ClientRepository.class);
    userServiceClient = mock(UserServiceClient.class);
    clientService = new ClientService(clientRepository, new ClientMapper(), userServiceClient);
    when(clientRepository.saveAndFlush(any(Client.class))).thenAnswer(call -> call.getArgument(0));
  }

  /**
   *
   * Builds a valid request.
   *
   * @return the request
   *
   */
  private static ClientRequestDto request() {
    final ClientRequestDto request = new ClientRequestDto();
    request.setFirstName("John");
    request.setType(ClientType.INDIVIDUAL);
    request.setStatus(ClientStatus.ACTIVE);
    request.setLastName("Doe");
    request.setEmail("John.Doe@example.com");
    return request;
  }

  /**
   *
   * The user is created with a derived username and the returned identifier is stored.
   *
   */
  @Test
  void createStoresTheUserIdOfTheCreatedUser() {
    final UUID userId = UUID.randomUUID();
    when(userServiceClient.createUser(any())).thenReturn(
        new UserServiceCreationResponseDto(userId, "john.doe", "John.Doe@example.com", new LinkedHashSet<>()));

    final ClientDto result = clientService.create(request());

    final ArgumentCaptor<UserServiceCreationRequestDto> captor = ArgumentCaptor.forClass(UserServiceCreationRequestDto.class);
    verify(userServiceClient).createUser(captor.capture());
    assertThat(captor.getValue().getUsername()).isEqualTo("john.doe");
    assertThat(captor.getValue().getEmail()).isEqualTo("John.Doe@example.com");
    assertThat(captor.getValue().getPassword()).hasSize(12);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFirstName()).isEqualTo("John");
  }

  /**
   *
   * A missing email is rejected before any call to the user service.
   *
   */
  @Test
  void createWithoutEmailIsBadRequest() {
    final ClientRequestDto request = request();
    request.setEmail(" ");

    assertThatThrownBy(() -> clientService.create(request)).isInstanceOfSatisfying(ResponseStatusException.class,
        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    verify(userServiceClient, never()).createUser(any());
  }

  /**
   *
   * User-service failures are translated and nothing is persisted.
   *
   */
  @Test
  void createTranslatesUserServiceFailures() {
    assertCreateFails(HttpClientErrorException.create(HttpStatus.CONFLICT, "c", null, null, null), HttpStatus.CONFLICT);
    assertCreateFails(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "b", null, null, null),
        HttpStatus.BAD_REQUEST);
    assertCreateFails(HttpClientErrorException.create(HttpStatus.FORBIDDEN, "f", null, null, null),
        HttpStatus.BAD_GATEWAY);
    assertCreateFails(HttpServerErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "s", null, null, null),
        HttpStatus.BAD_GATEWAY);
    assertCreateFails(new ResourceAccessException("down"), HttpStatus.SERVICE_UNAVAILABLE);
    assertCreateFails(new IllegalStateException("boom"), HttpStatus.INTERNAL_SERVER_ERROR);
    verify(clientRepository, never()).saveAndFlush(any());
  }

  /**
   *
   * A response without user identifier is a bad gateway.
   *
   */
  @Test
  void createWithoutUserIdInResponseIsBadGateway() {
    when(userServiceClient.createUser(any())).thenReturn(new UserServiceCreationResponseDto());

    assertThatThrownBy(() -> clientService.create(request())).isInstanceOfSatisfying(ResponseStatusException.class,
        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY));
  }

  /**
   *
   * The created user is deleted when the client cannot be persisted.
   *
   */
  @Test
  void createCompensatesUserWhenPersistenceFails() {
    final UUID userId = UUID.randomUUID();
    when(userServiceClient.createUser(any())).thenReturn(
        new UserServiceCreationResponseDto(userId, "john.doe", "x@y.z", new LinkedHashSet<>()));
    when(clientRepository.saveAndFlush(any(Client.class))).thenThrow(new IllegalStateException("db"));

    assertThatThrownBy(() -> clientService.create(request())).isInstanceOf(IllegalStateException.class);

    verify(userServiceClient).deleteUser(userId);
  }

  /**
   *
   * A failed compensation does not mask the original error.
   *
   */
  @Test
  void failedCompensationDoesNotMaskOriginalError() {
    final UUID userId = UUID.randomUUID();
    when(userServiceClient.createUser(any())).thenReturn(
        new UserServiceCreationResponseDto(userId, "john.doe", "x@y.z", new LinkedHashSet<>()));
    when(clientRepository.saveAndFlush(any(Client.class))).thenThrow(new IllegalStateException("db"));
    doThrow(new ResourceAccessException("down")).when(userServiceClient).deleteUser(userId);

    assertThatThrownBy(() -> clientService.create(request()))
        .isInstanceOf(IllegalStateException.class).hasMessage("db");
  }

  /**
   *
   * Unknown clients give a 404 on read, update and delete.
   *
   */
  @Test
  void unknownClientIsNotFound() {
    final UUID id = UUID.randomUUID();
    when(clientRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> clientService.findById(id)).isInstanceOfSatisfying(ResponseStatusException.class,
        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    assertThatThrownBy(() -> clientService.update(id, request())).isInstanceOf(ResponseStatusException.class);
    assertThatThrownBy(() -> clientService.delete(id)).isInstanceOf(ResponseStatusException.class);
  }

  /**
   *
   * Update changes the names and keeps the user identifier.
   *
   */
  @Test
  void updateChangesNamesOnly() {
    final UUID userId = UUID.randomUUID();
    final Client client = new Client("Old", "Name", userId, ClientType.INDIVIDUAL, ClientStatus.ACTIVE);
    when(clientRepository.findById(any())).thenReturn(Optional.of(client));

    final ClientDto result = clientService.update(UUID.randomUUID(), request());

    assertThat(result.getFirstName()).isEqualTo("John");
    assertThat(result.getLastName()).isEqualTo("Doe");
    assertThat(result.getUserId()).isEqualTo(userId);
  }

  /**
   *
   * Asserts that the creation fails with the expected status when the user service throws.
   *
   * @param failure the failure thrown by the user-service client
   * @param expected the expected HTTP status
   *
   */
  private void assertCreateFails(RuntimeException failure, HttpStatus expected) {
    doThrow(failure).when(userServiceClient).createUser(any());

    assertThatThrownBy(() -> clientService.create(request())).isInstanceOfSatisfying(ResponseStatusException.class,
        e -> assertThat(e.getStatusCode()).isEqualTo(expected));
  }
}
