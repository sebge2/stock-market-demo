package be.sgerard.demo.employee.mapper;

import be.sgerard.demo.employee.model.Department;
import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.dto.EmployeeDto;
import be.sgerard.demo.employee.model.dto.EmployeeRequestDto;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 *
 * Maps employee entities to their API representations.
 *
 */
@Component
public class EmployeeMapper {

  /**
   *
   * Creates an employee entity from request data with a provided userId.
   *
   * @param request the employee request DTO
   * @param department the employee's department
   * @param userId the user-service identifier
   * @return the created employee entity
   *
   */
  public Employee toEntity(EmployeeRequestDto request, Department department, UUID userId) {
    return new Employee(request.getFirstName(), request.getLastName(), request.getEmail(),
        userId, request.getStatus(), department);
  }

  /**
   *
   * Creates an employee entity from request data (backward compatibility).
   * Uses the userId from the request if available.
   *
   * @param request the employee request DTO
   * @param department the employee's department
   * @return the created employee entity
   *

   */
  public Employee toEntity(EmployeeRequestDto request, Department department) {
    return toEntity(request, department, request.getUserId());
  }

  /**
   *
   * Updates an employee entity with mutable request data.
   *

   * @param request the employee request DTO
   * @param employee the employee entity to update
   * @param department the employee's department
   *
   */
  public void updateEntity(EmployeeRequestDto request, Employee employee,
      Department department) {
    employee.setFirstName(request.getFirstName());
    employee.setLastName(request.getLastName());
    employee.setEmail(request.getEmail());
    employee.setStatus(request.getStatus());
    employee.setDepartment(department);
  }

  /**
   *
   * Converts an employee entity to a response DTO.
   *
   * @param employee the employee entity
   * @return the employee response DTO
   *
   */
  public EmployeeDto toDto(Employee employee) {
    EmployeeDto dto = new EmployeeDto();
    dto.setId(employee.getId());
    dto.setUserId(employee.getUserId());
    dto.setFirstName(employee.getFirstName());
    dto.setLastName(employee.getLastName());
    dto.setEmail(employee.getEmail());
    dto.setStatus(employee.getStatus());
    dto.setDepartmentId(
        employee.getDepartment() == null ? null : employee.getDepartment().getId());
    dto.setCreatedAt(employee.getCreatedAt());
    dto.setUpdatedAt(employee.getUpdatedAt());
    return dto;
  }
}
