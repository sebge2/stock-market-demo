package be.sgerard.demo.employee.service;

import be.sgerard.demo.employee.client.UserServiceClient;
import be.sgerard.demo.employee.mapper.EmployeeMapper;
import be.sgerard.demo.employee.model.Department;
import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.dto.EmployeeDto;
import be.sgerard.demo.employee.model.dto.EmployeeRequestDto;
import be.sgerard.demo.employee.model.dto.UserServiceCreationRequestDto;
import be.sgerard.demo.employee.model.dto.UserServiceCreationResponseDto;
import be.sgerard.demo.employee.repository.DepartmentRepository;
import be.sgerard.demo.employee.repository.EmployeeRepository;
import be.sgerard.demo.employee.util.UserCreationHelper;
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
 * Provides CRUD operations for employees.
 *
 */
@Service
@Transactional(readOnly = true)
public class EmployeeService {

  /**
   *
   * Logger.
   *
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

  /**
   *
   * Repository for employee persistence.
   *
   */
  private final EmployeeRepository employeeRepository;

  /**
   *
   * Repository for department persistence.
   *
   */
  private final DepartmentRepository departmentRepository;

  /**
   *
   * Mapper for employee entity/DTO conversion.
   *
   */
  private final EmployeeMapper employeeMapper;

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
   * @param employeeRepository the employee repository
   * @param departmentRepository the department repository
   * @param employeeMapper the employee mapper
   * @param userServiceClient the user-service client
   *
   */
  public EmployeeService(EmployeeRepository employeeRepository,
      DepartmentRepository departmentRepository,
      EmployeeMapper employeeMapper,
      UserServiceClient userServiceClient) {
    this.employeeRepository = employeeRepository;
    this.departmentRepository = departmentRepository;
    this.employeeMapper = employeeMapper;
    this.userServiceClient = userServiceClient;
  }

  /**
   *
   * Returns a page of employees.
   *
   * @param pageable the pagination information
   * @return the page of employees
   *
   */
  public Page<EmployeeDto> findAll(Pageable pageable) {
    return employeeRepository.findAll(pageable).map(employeeMapper::toDto);
  }

  /**
   *
   * Returns an employee by identifier.
   *
   * @param id the employee identifier
   * @return the employee DTO
   *
   */
  public EmployeeDto findById(UUID id) {
    return employeeMapper.toDto(findEntity(id));
  }

  /**
   *
   * Creates an employee and the corresponding user in the user-service (auto-generated username and
   * temporary password). The user is created before the employee is persisted: if its creation fails
   * nothing is persisted.
   *
   * @param request the employee creation request
   * @return the created employee DTO
   * @throws ResponseStatusException if the email is in use, the department is unknown or the user
   *     creation fails
   *
   */
  @Transactional
  public EmployeeDto create(EmployeeRequestDto request) {
    ensureEmailAvailable(request.getEmail(), null);
    final Department department = findDepartment(request.getDepartmentId());

    LOGGER.info("Creating user in user-service for employee with email: {}", request.getEmail());
    final UUID userId = createUserInUserService(request);

    final Employee employee = employeeMapper.toEntity(request, department, userId);
    final EmployeeDto result;
    try {
      result = employeeMapper.toDto(employeeRepository.saveAndFlush(employee));
    } catch (RuntimeException e) {
      compensateUserCreation(userId);
      throw e;
    }
    LOGGER.info("Employee created successfully with ID: {}", result.getId());
    return result;
  }

  /**
   *
   * Best-effort compensation: deletes the user created in the user-service because the employee
   * could not be persisted. A failure is only logged so that the original error is not masked.
   * Failures raised at commit time (after this method returns) are not compensated.
   *
   * @param userId the identifier of the user to delete
   *
   */
  private void compensateUserCreation(UUID userId) {
    try {
      userServiceClient.deleteUser(userId);
      LOGGER.warn("Employee persistence failed, user {} deleted from the user-service", userId);
    } catch (RuntimeException e) {
      LOGGER.error("Employee persistence failed and user {} could not be deleted (orphan user)",
          userId, e);
    }
  }

  /**
   *
   * Updates an employee.
   *
   * @param id the employee identifier
   * @param request the employee update request
   * @return the updated employee DTO
   *
   */
  @Transactional
  public EmployeeDto update(UUID id, EmployeeRequestDto request) {
    final Employee employee = findEntity(id);
    ensureEmailAvailable(request.getEmail(), id);
    employeeMapper.updateEntity(request, employee, findDepartment(request.getDepartmentId()));
    return employeeMapper.toDto(employeeRepository.save(employee));
  }

  /**
   *
   * Deletes an employee.
   *
   * @param id the employee identifier
   *
   */
  @Transactional
  public void delete(UUID id) {
    employeeRepository.delete(findEntity(id));
  }

  /**
   *
   * Creates a user in the user-service with auto-generated credentials and translates the failures
   * to ResponseStatusExceptions: 409 for a username conflict, 400 for a rejected request, 502 for an
   * unexpected upstream answer, 503 when the user-service is unreachable and 500 otherwise.
   *
   * @param request the employee creation request
   * @return the identifier of the created user
   * @throws ResponseStatusException if the user cannot be created
   *
   */
  private UUID createUserInUserService(EmployeeRequestDto request) {
    final String username = UserCreationHelper.generateUsername(request.getEmail());
    final UserServiceCreationRequestDto userRequest = new UserServiceCreationRequestDto(
        username,
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
   * Finds an optional department by ID.
   *
   * @param id the department identifier
   * @return the department or null if id is null
   * @throws ResponseStatusException if department not found
   *
   */
  private Department findDepartment(UUID id) {
    if (id == null) {
      return null;
    }
    return departmentRepository.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found: " + id));
  }

  /**
   *
   * Finds an employee by ID.
   *
   * @param id the employee identifier
   * @return the employee entity
   * @throws ResponseStatusException if employee not found
   *
   */
  private Employee findEntity(UUID id) {
    return employeeRepository.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + id));
  }

  /**
   *
   * Ensures that an email is not already in use by another employee.
   *
   * @param email the email to check
   * @param currentId the current employee ID (ignored in uniqueness check)
   * @throws ResponseStatusException if email already in use
   *
   */
  private void ensureEmailAvailable(String email, UUID currentId) {
    employeeRepository.findByEmail(email)
        .filter(employee -> !employee.getId().equals(currentId))
        .ifPresent(employee -> {
          throw new ResponseStatusException(HttpStatus.CONFLICT,
              "Employee email already exists: " + email);
        });
  }
}
