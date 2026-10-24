package be.sgerard.demo.employee.service;

import be.sgerard.demo.employee.mapper.EmployeeMapper;
import be.sgerard.demo.employee.model.Department;
import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.model.Role;
import be.sgerard.demo.employee.model.dto.EmployeeDto;
import be.sgerard.demo.employee.model.dto.EmployeeRequestDto;
import be.sgerard.demo.employee.repository.DepartmentRepository;
import be.sgerard.demo.employee.repository.EmployeeRepository;
import be.sgerard.demo.employee.repository.RoleRepository;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Provides CRUD operations for employees.
 *
 */
@Service
@Transactional(readOnly = true)
public class EmployeeService {
  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final RoleRepository roleRepository;
  private final EmployeeMapper employeeMapper;

  /** Creates the service. */
  public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository,
      RoleRepository roleRepository, EmployeeMapper employeeMapper) {
    this.employeeRepository = employeeRepository; this.departmentRepository = departmentRepository;
    this.roleRepository = roleRepository; this.employeeMapper = employeeMapper;
  }

  /** Returns a page of employees. */
  public Page<EmployeeDto> findAll(Pageable pageable) { return employeeRepository.findAll(pageable).map(employeeMapper::toDto); }
  /** Returns an employee. */
  public EmployeeDto findById(UUID id) { return employeeMapper.toDto(findEntity(id)); }

  /** Creates an employee. */
  @Transactional
  public EmployeeDto create(EmployeeRequestDto request) {
    ensureEmailAvailable(request.getEmail(), null);
    Employee employee = employeeMapper.toEntity(request, findDepartment(request.getDepartmentId()));
    replaceRoles(employee, request.getRoleIds());
    return employeeMapper.toDto(employeeRepository.save(employee));
  }

  /** Updates an employee. */
  @Transactional
  public EmployeeDto update(UUID id, EmployeeRequestDto request) {
    Employee employee = findEntity(id); ensureEmailAvailable(request.getEmail(), id);
    employeeMapper.updateEntity(request, employee, findDepartment(request.getDepartmentId()));
    replaceRoles(employee, request.getRoleIds());
    return employeeMapper.toDto(employeeRepository.save(employee));
  }

  /** Deletes an employee. */
  @Transactional
  public void delete(UUID id) { employeeRepository.delete(findEntity(id)); }

  /** Replaces role assignments. */
  private void replaceRoles(Employee employee, Set<UUID> roleIds) {
    Set<Role> roles = new LinkedHashSet<>();
    for (UUID roleId : roleIds) roles.add(roleRepository.findById(roleId).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + roleId)));
    new LinkedHashSet<>(employee.getRoles()).forEach(employee::removeRole); roles.forEach(employee::addRole);
  }
  /** Finds an optional department. */
  private Department findDepartment(UUID id) {
    if (id == null) return null;
    return departmentRepository.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found: " + id));
  }
  /** Finds an employee. */
  private Employee findEntity(UUID id) { return employeeRepository.findById(id).orElseThrow(
      () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + id)); }
  /** Ensures email uniqueness. */
  private void ensureEmailAvailable(String email, UUID currentId) {
    employeeRepository.findByEmail(email).filter(employee -> !employee.getId().equals(currentId))
        .ifPresent(employee -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee email already exists: " + email); });
  }
}
