package be.sgerard.demo.employee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.sgerard.demo.employee.client.IUserServiceClient;
import be.sgerard.demo.employee.mapper.EmployeeMapper;
import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.EmployeeStatus;
import be.sgerard.demo.employee.model.dto.EmployeeDto;
import be.sgerard.demo.employee.model.dto.EmployeeRequestDto;
import be.sgerard.demo.employee.model.dto.UserServiceCreationResponseDto;
import be.sgerard.demo.employee.repository.DepartmentRepository;
import be.sgerard.demo.employee.repository.EmployeeRepository;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Unit tests for EmployeeService.create() method with user-service integration.
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
   * Mock department repository.
   *
   */
  private DepartmentRepository departmentRepository;

  /**
   *
   * Real employee mapper (not mocked to avoid Mockito issues).
   *
   */
  private EmployeeMapper employeeMapper;

  /**
   *
   * Mock user-service client.
   *
   */
  private IUserServiceClient userServiceClient;

  /**
   *
   * Service under test.
   *
   */
  private EmployeeService employeeService;

  /**
   *
   * Setup before each test.
   *
   */
  @BeforeEach
  void setUp() {
    employeeRepository = mock(EmployeeRepository.class);
    departmentRepository = mock(DepartmentRepository.class);
    employeeMapper = new EmployeeMapper();
    userServiceClient = mock(IUserServiceClient.class);

    employeeService =
        new EmployeeService(employeeRepository, departmentRepository, employeeMapper,
            userServiceClient);
  }

  /**
   *
   * Test happy path: user created in user-service, employee created successfully.
   *
   */
  @Test
  void testCreateEmployeeSuccessfullyCreatesUserAndEmployee() {
    // Arrange
    final EmployeeRequestDto request = new EmployeeRequestDto();
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setEmail("john.doe@example.com");
    request.setStatus(EmployeeStatus.ACTIVE);
    request.setDepartmentId(null);

    final UUID userId = UUID.randomUUID();
    final UserServiceCreationResponseDto userResponse = new UserServiceCreationResponseDto(
        userId, "john.doe", "john.doe@example.com", new LinkedHashSet<>());

    final Employee savedEmployee = new Employee("John", "Doe", "john.doe@example.com", userId,
        EmployeeStatus.ACTIVE, null);

    when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
    when(userServiceClient.createUser(any())).thenReturn(userResponse);
    when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

    // Act
    EmployeeDto result = employeeService.create(request);

    // Assert
    assertNotNull(result);
    assertEquals(userId, result.getUserId());
    verify(userServiceClient, times(1)).createUser(any());
    verify(employeeRepository, times(1)).save(any(Employee.class));
  }

  /**
   *
   * Test backward compatibility: userId provided in request, user-service not called.
   *
   */
  @Test
  void testCreateEmployeeWithProvidedUserIdSkipsUserServiceCall() {
    // Arrange
    final UUID providedUserId = UUID.randomUUID();
    final EmployeeRequestDto request = new EmployeeRequestDto();
    request.setFirstName("Jane");
    request.setLastName("Smith");
    request.setEmail("jane.smith@example.com");
    request.setStatus(EmployeeStatus.ACTIVE);
    request.setUserId(providedUserId);
    request.setDepartmentId(null);

    final Employee savedEmployee = new Employee("Jane", "Smith", "jane.smith@example.com",
        providedUserId, EmployeeStatus.ACTIVE, null);

    when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
    when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

    // Act
    EmployeeDto result = employeeService.create(request);

    // Assert
    assertNotNull(result);
    verify(userServiceClient, never()).createUser(any());
    verify(employeeRepository, times(1)).save(any(Employee.class));
  }

  /**
   *
   * Test user-service returns 4xx error: should not retry and throw exception.
   *
   */
  @Test
  void testCreateEmployeeWhen4xxErrorThrownByUserService() {
    // Arrange
    final EmployeeRequestDto request = new EmployeeRequestDto();
    request.setFirstName("Bob");
    request.setLastName("Johnson");
    request.setEmail("bob.johnson@example.com");
    request.setStatus(EmployeeStatus.ACTIVE);
    request.setDepartmentId(null);

    when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
    when(userServiceClient.createUser(any())).thenThrow(
        new HttpClientErrorException(HttpStatus.CONFLICT, "Username already exists"));

    // Act & Assert
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> employeeService.create(request));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    verify(userServiceClient, times(1)).createUser(any());
    verify(employeeRepository, never()).save(any());
  }

  /**
   *
   * Test user-service call fails after retries: employee should not be persisted.
   *
   */
  @Test
  void testCreateEmployeeWhenUserServiceUnavailable() {
    // Arrange
    final EmployeeRequestDto request = new EmployeeRequestDto();
    request.setFirstName("Alice");
    request.setLastName("Brown");
    request.setEmail("alice.brown@example.com");
    request.setStatus(EmployeeStatus.ACTIVE);
    request.setDepartmentId(null);

    when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
    when(userServiceClient.createUser(any()))
        .thenThrow(new RuntimeException("Service unavailable"));

    // Act & Assert
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> employeeService.create(request));

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    verify(employeeRepository, never()).save(any());
  }

  /**
   *
   * Test email already in use: should throw conflict exception before calling user-service.
   *
   */
  @Test
  void testCreateEmployeeWhenEmailAlreadyInUse() {
    // Arrange
    final EmployeeRequestDto request = new EmployeeRequestDto();
    request.setFirstName("Charlie");
    request.setLastName("Davis");
    request.setEmail("charlie.davis@example.com");
    request.setStatus(EmployeeStatus.ACTIVE);
    request.setDepartmentId(null);

    final UUID existingEmployeeId = UUID.randomUUID();
    final Employee existingEmployee = new Employee("Charlie", "Davis", request.getEmail(),
        UUID.randomUUID(), EmployeeStatus.ACTIVE, null);
    // Use ReflectionTestUtils to set ID since it's JPA managed
    ReflectionTestUtils.setField(existingEmployee, "id", existingEmployeeId);

    when(employeeRepository.findByEmail(request.getEmail()))
        .thenReturn(Optional.of(existingEmployee));

    // Act & Assert
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> employeeService.create(request));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    verify(userServiceClient, never()).createUser(any());
    verify(employeeRepository, never()).save(any());
  }
}
