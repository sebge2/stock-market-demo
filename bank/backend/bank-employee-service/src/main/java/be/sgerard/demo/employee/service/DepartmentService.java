package be.sgerard.demo.employee.service;

import be.sgerard.demo.employee.mapper.DepartmentMapper;
import be.sgerard.demo.employee.model.Department;
import be.sgerard.demo.employee.model.dto.DepartmentDto;
import be.sgerard.demo.employee.model.dto.DepartmentRequestDto;
import be.sgerard.demo.employee.model.Employee;
import be.sgerard.demo.employee.repository.DepartmentRepository;
import be.sgerard.demo.employee.repository.EmployeeRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Provides the business operations for managing departments.
 *
 */
@Service
@Transactional(readOnly = true)
public class DepartmentService {

  /**
   *
   * Repository used to persist departments.
   *
   */
  private final DepartmentRepository departmentRepository;

  /**
   *
   * Mapper used to translate domain objects to API objects.
   *
   */
  private final DepartmentMapper departmentMapper;

  /**
   *
   * Employee persistence, used to resolve department managers.
   *
   */
  private final EmployeeRepository employeeRepository;

  /**
   *
   * Creates the department service.
   *
   * @param departmentRepository repository used to persist departments
   * @param departmentMapper mapper used to translate departments
   */
  public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper,
      EmployeeRepository employeeRepository) {
    this.departmentRepository = departmentRepository;
    this.departmentMapper = departmentMapper;
    this.employeeRepository = employeeRepository;
  }

  /**
   *
   * Returns all departments.
   *
   * @return all department responses
   */
  public List<DepartmentDto> findAll() {
    return departmentRepository.findAll().stream().map(departmentMapper::toDto).toList();
  }

  /**
   *
   * Returns a department by identifier.
   *
   * @param id department identifier
   * @return the department response
   */
  public DepartmentDto findById(UUID id) {
    return departmentMapper.toDto(findEntity(id));
  }

  /**
   *
   * Creates a new department.
   *
   * @param request creation data
   * @return the created department response
   */
  @Transactional
  public DepartmentDto create(DepartmentRequestDto request) {
    ensureNameIsAvailable(request.getName(), null);
    Department department = departmentMapper.toEntity(request, findManager(request.getManagerId(), null),
        findParent(request.getParentId(), null));
    final Department saved = departmentRepository.save(department);
    attachManager(saved);
    return departmentMapper.toDto(saved);
  }

  /**
   *
   * Replaces a department's mutable data.
   *
   * @param id department identifier
   * @param request replacement data
   * @return the updated department response
   */
  @Transactional
  public DepartmentDto update(UUID id, DepartmentRequestDto request) {
    Department department = findEntity(id);
    ensureNameIsAvailable(request.getName(), id);
    departmentMapper.updateEntity(request, department, findManager(request.getManagerId(), id),
        findParent(request.getParentId(), department));
    final Department saved = departmentRepository.save(department);
    attachManager(saved);
    return departmentMapper.toDto(saved);
  }

  /**
   *
   * Deletes a department by identifier.
   *
   * @param id department identifier
   */
  @Transactional
  public void delete(UUID id) {
    final Department department = findEntity(id);
    if (departmentRepository.existsByParentId(id)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Department has sub-departments: " + id);
    }
    departmentRepository.delete(department);
  }

  /**
   *
   * Resolves the manager of a department.
   *
   * @param managerId identifier of the manager, may be null
   * @return the manager, or null when no manager is given
   */
  /**
   *
   * Makes the manager of a department a member of that department.
   *
   * @param department the saved department
   *
   */
  private void attachManager(Department department) {
    final Employee manager = department.getManager();
    if (manager != null) {
      manager.setDepartment(department);
    }
  }

  private Employee findManager(UUID managerId, UUID currentId) {
    if (managerId == null) {
      return null;
    }
    if (departmentRepository.findByManagerId(managerId).stream().anyMatch(d -> !d.getId().equals(currentId))) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee already manages another department: " + managerId);
    }
    return employeeRepository.findById(managerId).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found: " + managerId));
  }

  /**
   *
   * Retrieves a department entity or reports a missing resource.
   *
   * @param id department identifier
   * @return the matching entity
   */
  /**
   *
   * Finds the parent department, making sure the hierarchy stays a tree.
   *
   * @param parentId identifier of the parent, if any
   * @param department the edited department, or {@code null} when it is being created
   * @return the parent department, or {@code null}
   *
   */
  private Department findParent(UUID parentId, Department department) {
    if (parentId == null) {
      return null;
    }
    final Department parent = findEntity(parentId);
    for (Department ancestor = parent; department != null && ancestor != null; ancestor = ancestor.getParent()) {
      if (ancestor.getId().equals(department.getId())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A department cannot be its own ancestor: " + parentId);
      }
    }
    return parent;
  }

  private Department findEntity(UUID id) {
    return departmentRepository.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found: " + id));
  }

  /**
   *
   * Ensures a name is not already used by a different department.
   *
   * @param name proposed department name
   * @param currentId identifier of the department being updated, if any
   */
  private void ensureNameIsAvailable(String name, UUID currentId) {
    departmentRepository.findByName(name)
        .filter(department -> !department.getId().equals(currentId))
        .ifPresent(department -> {
          throw new ResponseStatusException(HttpStatus.CONFLICT,
              "Department name already exists: " + name);
        });
  }
}
