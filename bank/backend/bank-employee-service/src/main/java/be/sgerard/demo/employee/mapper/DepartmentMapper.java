package be.sgerard.demo.employee.mapper;

import be.sgerard.demo.employee.model.Department;
import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.dto.DepartmentDto;
import be.sgerard.demo.employee.model.dto.DepartmentRequestDto;
import org.springframework.stereotype.Component;

/**
 *
 * Maps department entities to and from their API representations.
 *
 */
@Component
public class DepartmentMapper {

  /**
   *
   * Creates a department entity from a request payload.
   *
   * @param request source request
   * @param manager manager of the department, may be null
   * @return a new department entity
   */
  public Department toEntity(DepartmentRequestDto request, Employee manager) {
    final Department department = new Department(request.getName(), request.getDescription(), request.getStatus());
    department.setManager(manager);
    return department;
  }

  /**
   *
   * Copies mutable request data into an existing department.
   *
   * @param request source request
   * @param department target department
   * @param manager manager of the department, may be null
   */
  public void updateEntity(DepartmentRequestDto request, Department department, Employee manager) {
    department.setName(request.getName());
    department.setDescription(request.getDescription());
    department.setStatus(request.getStatus());
    department.setManager(manager);
  }

  /**
   *
   * Converts a department entity to its API representation.
   *
   * @param department source department
   * @return the department response
   */
  public DepartmentDto toDto(Department department) {
    DepartmentDto dto = new DepartmentDto();
    dto.setId(department.getId());
    dto.setName(department.getName());
    dto.setDescription(department.getDescription());
    dto.setStatus(department.getStatus());
    dto.setManagerId(department.getManager() == null ? null : department.getManager().getId());
    dto.setCreatedAt(department.getCreatedAt());
    dto.setUpdatedAt(department.getUpdatedAt());
    return dto;
  }
}
