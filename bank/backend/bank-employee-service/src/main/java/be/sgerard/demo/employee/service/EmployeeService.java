package be.sgerard.demo.employee.service;

import be.sgerard.demo.employee.client.IUserServiceClient;
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
  private final IUserServiceClient userServiceClient;

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
      IUserServiceClient userServiceClient) {
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
   * Creates an employee and automatically creates a corresponding user in the user-service.
   * If userId is provided in the request, it's used directly (backward compatibility).
   * Otherwise, a user is created in the user-service with auto-generated credentials.
   * This operation is atomic: if user creation fails, the employee is not persisted.
   *
   * @param request the employee creation request
   * @return the created employee DTO
   * @throws ResponseStatusException if email is already in use or department not found
   * @throws ResponseStatusException if user creation fails
   *
   */
  @Transactional
  public EmployeeDto create(EmployeeRequestDto request) {
    ensureEmailAvailable(request.getEmail(), null);
    final Department department = findDepartment(request.getDepartmentId());

    final UUID userId;
    if (request.getUserId() != null) {
      // Backward compatibility: if userId is provided, use it directly
      LOGGER.info("Creating employee with provided userId: {}", request.getUserId());
      userId = request.getUserId();
    } else {
      // Create user in user-service
      LOGGER.info("Creating user in user-service for employee with email: {}", request.getEmail());
      final UserServiceCreationResponseDto userResponse = createUserInUserService(request);
      userId = userResponse.getId();
    }

    // Create employee with the obtained userId
    Employee employee = employeeMapper.toEntity(request, department, userId);
    final EmployeeDto result = employeeMapper.toDto(employeeRepository.save(employee));
    LOGGER.info("Employee created successfully with ID: {}", result.getId());
    return result;
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
    Employee employee = findEntity(id);
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
   * Creates a user in the user-service with auto-generated credentials.
   * Translates user-service errors to appropriate ResponseStatusExceptions.
   *
   * @param request the employee creation request containing email
   * @return the user service response with created user ID
   * @throws ResponseStatusException with 502/503 if user creation fails
   *
   */
  private UserServiceCreationResponseDto createUserInUserService(
      EmployeeRequestDto request) {
    final String username = UserCreationHelper.generateUsername(request.getEmail());
    final String password = UserCreationHelper.generateTemporaryPassword();

    final UserServiceCreationRequestDto userRequest = new UserServiceCreationRequestDto(
        username,
        request.getEmail(),
        password,
        UserCreationHelper.createDefaultRoleIds()
    );

    try {
      return userServiceClient.createUser(userRequest);
    } catch (HttpClientErrorException e) {
      // 4xx errors from user-service (validation, conflict, etc.)
      LOGGER.error("User-service returned 4xx error: {} - {}", e.getStatusCode(),
          e.getResponseBodyAsString());
      if (e.getStatusCode() == HttpStatus.CONFLICT) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
            "Username already exists in user-service: " + username);
      }
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Failed to create user: " + e.getMessage());
    } catch (Exception e) {
      // Transient errors (after retries exhausted)
      LOGGER.error("User-service call failed after retries: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
          "User service unavailable. Failed to create user. Please retry.");
    }
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
