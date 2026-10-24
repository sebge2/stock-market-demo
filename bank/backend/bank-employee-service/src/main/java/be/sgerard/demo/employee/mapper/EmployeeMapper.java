package be.sgerard.demo.employee.mapper;

import be.sgerard.demo.employee.model.Department;
import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.dto.EmployeeDto;
import be.sgerard.demo.employee.model.dto.EmployeeRequestDto;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * Maps employee entities to their API representations.
 *
 */
@Component
public class EmployeeMapper {
  /** Creates an employee entity. */
  public Employee toEntity(EmployeeRequestDto request, Department department) {
    return new Employee(request.getFirstName(), request.getLastName(), request.getEmail(), request.getEncryptedPassword(), request.getStatus(), department);
  }
  /** Copies mutable request data. */
  public void updateEntity(EmployeeRequestDto request, Employee employee, Department department) {
    employee.setFirstName(request.getFirstName()); employee.setLastName(request.getLastName());
    employee.setEmail(request.getEmail()); employee.setEncryptedPassword(request.getEncryptedPassword());
    employee.setStatus(request.getStatus()); employee.setDepartment(department);
  }
  /** Converts an entity to a response. */
  public EmployeeDto toDto(Employee employee) {
    EmployeeDto dto = new EmployeeDto(); dto.setId(employee.getId()); dto.setFirstName(employee.getFirstName());
    dto.setLastName(employee.getLastName()); dto.setEmail(employee.getEmail()); dto.setStatus(employee.getStatus());
    dto.setDepartmentId(employee.getDepartment() == null ? null : employee.getDepartment().getId());
    dto.setRoleIds(employee.getRoles().stream().map(role -> role.getId()).collect(Collectors.toCollection(LinkedHashSet::new)));
    dto.setCreatedAt(employee.getCreatedAt()); dto.setUpdatedAt(employee.getUpdatedAt()); return dto;
  }
}
