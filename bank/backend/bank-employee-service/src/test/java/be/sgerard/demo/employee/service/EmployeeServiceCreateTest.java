package be.sgerard.demo.employee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.sgerard.demo.employee.client.UserServiceClient;
import be.sgerard.demo.employee.mapper.EmployeeMapper;
import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.EmployeeStatus;
import be.sgerard.demo.employee.model.dto.EmployeeDto;
import be.sgerard.demo.employee.model.dto.EmployeeRequestDto;
import be.sgerard.demo.employee.model.dto.UserServiceCreationRequestDto;
import be.sgerard.demo.employee.model.dto.UserServiceCreationResponseDto;
import be.sgerard.demo.employee.repository.DepartmentRepository;
import be.sgerard.demo.employee.repository.EmployeeRepository;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Unit tests of EmployeeService.create() and its integration with the user-service client.
 *
 */
class EmployeeServiceCreateTest {

  /**
   *
   * Mock employee repository.
   *
   */
  private EmployeeRepository employeeRepository;

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
  private EmployeeService employeeService;

  /**
   *
   * Creates the service under test with mocked collaborators.
   *
   */
  @BeforeEach
  void setUp() {
    employeeRepository = mock(EmployeeRepository.class);
    userServiceClient = mock(UserServiceClient.class);
    employeeService = new EmployeeService(employeeRepository, mock(DepartmentRepository.class),
        new EmployeeMapper(), userServiceClient);
    when(employeeRepository.findByEmail(any())).thenReturn(Optional.empty());
  }

  /**
   *
   * Builds a valid employee request.
   *
   * @param email the employee email
   * @return the request
   *
   */
  private EmployeeRequestDto request(String email) {
    final EmployeeRequestDto request = new EmployeeRequestDto();
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setEmail(email);
    request.setStatus(EmployeeStatus.ACTIVE);
    return request;
  }

  /**
   *
   * Asserts that the creation fails with the expected status and that nothing is persisted.
   *
   * @param expected the expected HTTP status
   *
   */
  private void assertCreationFailsWith(HttpStatus expected) {
    final ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> employeeService.create(request("john.doe@example.com")));

    assertEquals(expected, exception.getStatusCode());
    verify(employeeRepository, never()).saveAndFlush(any());
  }

  /**
   *
   * Happy path: the user is created from the email and its id is stored on the employee.
   *
   */
  @Test
  void createsUserThenEmployeeWithReturnedUserId() {
    final UUID userId = UUID.randomUUID();
    when(userServiceClient.createUser(any())).thenReturn(
        new UserServiceCreationResponseDto(userId, "john.doe", "john.doe@example.com",
            new LinkedHashSet<>()));
    when(employeeRepository.saveAndFlush(any(Employee.class))).thenAnswer(call -> call.getArgument(0));

    final EmployeeDto result = employeeService.create(request("John.Doe@example.com"));

    assertEquals(userId, result.getUserId());
    final ArgumentCaptor<UserServiceCreationRequestDto> captor =
        ArgumentCaptor.forClass(UserServiceCreationRequestDto.class);
    verify(userServiceClient, times(1)).createUser(captor.capture());
    assertEquals("john.doe", captor.getValue().getUsername());
    assertEquals("John.Doe@example.com", captor.getValue().getEmail());
    assertEquals(12, captor.getValue().getPassword().length());
    verify(employeeRepository, times(1)).saveAndFlush(any(Employee.class));
  }

  /**
   *
   * A 4xx conflict from the user-service is a 409 and nothing is persisted.
   *
   */
  @Test
  void usernameConflictIsReportedAsConflict() {
    when(userServiceClient.createUser(any())).thenThrow(
        new HttpClientErrorException(HttpStatus.CONFLICT, "Conflict"));

    assertCreationFailsWith(HttpStatus.CONFLICT);
    verify(userServiceClient, times(1)).createUser(any());
  }

  /**
   *
   * A 400 from the user-service is a 400 and nothing is persisted.
   *
   */
  @Test
  void validationErrorIsReportedAsBadRequest() {
    when(userServiceClient.createUser(any())).thenThrow(
        new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request"));

    assertCreationFailsWith(HttpStatus.BAD_REQUEST);
  }

  /**
   *
   * A 403 from the user-service (caller lacks USER_CREATE) is a 502, not a client error of this request.
   *
   */
  @Test
  void forbiddenByUserServiceIsReportedAsBadGateway() {
    when(userServiceClient.createUser(any())).thenThrow(
        new HttpClientErrorException(HttpStatus.FORBIDDEN, "Forbidden"));

    assertCreationFailsWith(HttpStatus.BAD_GATEWAY);
  }

  /**
   *
   * A 5xx still failing after the retries is a 502 and nothing is persisted.
   *
   */
  @Test
  void exhaustedServerErrorsAreReportedAsBadGateway() {
    when(userServiceClient.createUser(any())).thenThrow(
        new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    assertCreationFailsWith(HttpStatus.BAD_GATEWAY);
  }

  /**
   *
   * An unreachable user-service after the retries is a 503 and nothing is persisted.
   *
   */
  @Test
  void unreachableUserServiceIsReportedAsServiceUnavailable() {
    when(userServiceClient.createUser(any())).thenThrow(
        new ResourceAccessException("Read timed out"));

    assertCreationFailsWith(HttpStatus.SERVICE_UNAVAILABLE);
  }

  /**
   *
   * Any other failure is a 500 and nothing is persisted.
   *
   */
  @Test
  void unexpectedErrorIsReportedAsInternalServerError() {
    when(userServiceClient.createUser(any())).thenThrow(new IllegalStateException("no JWT"));

    assertCreationFailsWith(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   *
   * A user-service answer without identifier is a 502 and nothing is persisted.
   *
   */
  @Test
  void missingUserIdInResponseIsReportedAsBadGateway() {
    when(userServiceClient.createUser(any())).thenReturn(new UserServiceCreationResponseDto());

    assertCreationFailsWith(HttpStatus.BAD_GATEWAY);
  }

  /**
   *
   * An email already used by an employee is rejected before any call to the user-service.
   *
   */
  @Test
  void emailAlreadyInUseDoesNotCallUserService() {
    final Employee existing = new Employee("Jane", "Doe", "john.doe@example.com", UUID.randomUUID(),
        EmployeeStatus.ACTIVE, null);
    ReflectionTestUtils.setField(existing, "id", UUID.randomUUID());
    when(employeeRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(existing));

    assertCreationFailsWith(HttpStatus.CONFLICT);
    verify(userServiceClient, never()).createUser(any());
  }

  /**
   *
   * When the employee cannot be persisted the created user is deleted and the original error is kept.
   *
   */
  @Test
  void persistenceFailureDeletesCreatedUser() {
    final UUID userId = UUID.randomUUID();
    when(userServiceClient.createUser(any())).thenReturn(
        new UserServiceCreationResponseDto(userId, "john.doe", "john.doe@example.com",
            new LinkedHashSet<>()));
    when(employeeRepository.saveAndFlush(any(Employee.class)))
        .thenThrow(new IllegalStateException("constraint violation"));

    final IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> employeeService.create(request("john.doe@example.com")));

    assertEquals("constraint violation", exception.getMessage());
    verify(userServiceClient, times(1)).deleteUser(userId);
  }

  /**
   *
   * A failing compensation does not mask the original persistence error.
   *
   */
  @Test
  void failedCompensationKeepsOriginalError() {
    when(userServiceClient.createUser(any())).thenReturn(
        new UserServiceCreationResponseDto(UUID.randomUUID(), "john.doe", "john.doe@example.com",
            new LinkedHashSet<>()));
    when(employeeRepository.saveAndFlush(any(Employee.class)))
        .thenThrow(new IllegalStateException("constraint violation"));
    doThrow(new ResourceAccessException("down")).when(userServiceClient).deleteUser(any());

    final IllegalStateException exception = assertThrows(IllegalStateException.class,
        () -> employeeService.create(request("john.doe@example.com")));

    assertEquals("constraint violation", exception.getMessage());
  }

  /**
   *
   * An upstream 401/403 is not a client error of this request: it must not be reported as 400.
   *
   */
  @Test
  void upstreamForbiddenIsReportedAsBadGateway() {
    when(userServiceClient.createUser(any())).thenThrow(
        HttpClientErrorException.create(HttpStatus.FORBIDDEN, "Forbidden", null, null, null));

    assertCreationFailsWith(HttpStatus.BAD_GATEWAY);
  }
}
