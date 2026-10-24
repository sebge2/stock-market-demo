package be.sgerard.demo.employee.controller;

import be.sgerard.demo.employee.model.dto.DepartmentDto;
import be.sgerard.demo.employee.model.dto.DepartmentRequestDto;
import be.sgerard.demo.employee.service.DepartmentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Exposes the REST API used to manage bank departments.
 *
 */
@RestController
@RequestMapping("/departments")
public class DepartmentController {

  /**
   *
   * Service implementing department operations.
   *
   */
  private final DepartmentService departmentService;

  /**
   *
   * Creates the department controller.
   *
   * @param departmentService service implementing department operations
   */
  public DepartmentController(DepartmentService departmentService) {
    this.departmentService = departmentService;
  }

  /**
   *
   * Lists every department.
   *
   * @return all departments
   */
  @GetMapping
  public List<DepartmentDto> findAll() {
    return departmentService.findAll();
  }

  /**
   *
   * Retrieves one department.
   *
   * @param id department identifier
   * @return the department
   */
  @GetMapping("/{id}")
  public DepartmentDto findById(@PathVariable UUID id) {
    return departmentService.findById(id);
  }

  /**
   *
   * Creates a department.
   *
   * @param request department creation data
   * @return response containing the created department and its location
   */
  @PostMapping
  public ResponseEntity<DepartmentDto> create(@Valid @RequestBody DepartmentRequestDto request) {
    DepartmentDto department = departmentService.create(request);
    return ResponseEntity.created(URI.create("/departments/" + department.getId())).body(department);
  }

  /**
   *
   * Replaces a department.
   *
   * @param id department identifier
   * @param request replacement data
   * @return the updated department
   */
  @PutMapping("/{id}")
  public DepartmentDto update(@PathVariable UUID id, @Valid @RequestBody DepartmentRequestDto request) {
    return departmentService.update(id, request);
  }

  /**
   *
   * Deletes a department.
   *
   * @param id department identifier
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    departmentService.delete(id);
  }
}
