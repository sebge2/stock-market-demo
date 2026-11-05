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
   * Creates an employee entity from request data and the identifier of the user created for it.
   *
   * @param request the employee request DTO
   * @param department the employee's department
   * @param userId the user-service identifier
   * @return the created employee entity
   *
   */
  public Employee toEntity(EmployeeRequestDto request, Department department, UUID userId) {
    final Employee employee = new Employee(request.getFirstName(), request.getLastName(), request.getEmail(),
        userId, request.getStatus(), department);
    employee.setJobTitle(request.getJobTitle());
    return employee;
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
    employee.setJobTitle(request.getJobTitle());
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
    dto.setJobTitle(employee.getJobTitle());
    dto.setDepartmentId(
        employee.getDepartment() == null ? null : employee.getDepartment().getId());
    dto.setCreatedAt(employee.getCreatedAt());
    dto.setUpdatedAt(employee.getUpdatedAt());
    return dto;
  }
}
