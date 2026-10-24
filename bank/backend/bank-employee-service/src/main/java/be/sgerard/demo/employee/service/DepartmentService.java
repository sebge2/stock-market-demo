package be.sgerard.demo.employee.service;

import be.sgerard.demo.employee.mapper.DepartmentMapper;
import be.sgerard.demo.employee.model.Department;
import be.sgerard.demo.employee.model.dto.DepartmentDto;
import be.sgerard.demo.employee.model.dto.DepartmentRequestDto;
import be.sgerard.demo.employee.repository.DepartmentRepository;
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
   * Creates the department service.
   *
   * @param departmentRepository repository used to persist departments
   * @param departmentMapper mapper used to translate departments
   */
  public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
    this.departmentRepository = departmentRepository;
    this.departmentMapper = departmentMapper;
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
    Department department = departmentMapper.toEntity(request);
    return departmentMapper.toDto(departmentRepository.save(department));
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
    departmentMapper.updateEntity(request, department);
    return departmentMapper.toDto(departmentRepository.save(department));
  }

  /**
   *
   * Deletes a department by identifier.
   *
   * @param id department identifier
   */
  @Transactional
  public void delete(UUID id) {
    departmentRepository.delete(findEntity(id));
  }

  /**
   *
   * Retrieves a department entity or reports a missing resource.
   *
   * @param id department identifier
   * @return the matching entity
   */
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
