package be.sgerard.demo.employee.mapper;

import be.sgerard.demo.employee.model.Department;
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
   * @return a new department entity
   */
  public Department toEntity(DepartmentRequestDto request) {
    return new Department(request.getName(), request.getDescription(), request.getStatus());
  }

  /**
   *
   * Copies mutable request data into an existing department.
   *
   * @param request source request
   * @param department target department
   */
  public void updateEntity(DepartmentRequestDto request, Department department) {
    department.setName(request.getName());
    department.setDescription(request.getDescription());
    department.setStatus(request.getStatus());
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
    dto.setCreatedAt(department.getCreatedAt());
    dto.setUpdatedAt(department.getUpdatedAt());
    return dto;
  }
}
