package be.sgerard.demo.employee.controller;

import be.sgerard.demo.employee.model.dto.EmployeeDto;
import be.sgerard.demo.employee.model.dto.EmployeeRequestDto;
import be.sgerard.demo.employee.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

/**
 *
 * Exposes CRUD endpoints for employees.
 *
 */
@RestController
@RequestMapping("/employees")
@Validated
public class EmployeeController {
  private final EmployeeService employeeService;
  /** Creates the controller. */
  public EmployeeController(EmployeeService employeeService) { this.employeeService = employeeService; }
  /**
   *
   * Returns a page of employees selected through query parameters.
   *
   * @param page zero-based page number
   * @param size number of employees per page
   * @return the requested employee page
   */
  @GetMapping
  public Page<EmployeeDto> findAll(@RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
    return employeeService.findAll(pageable);
  }
  /** Returns an employee. */
  @GetMapping("/{id}")
  public EmployeeDto findById(@PathVariable UUID id) { return employeeService.findById(id); }
  /** Creates an employee. */
  @PostMapping
  public ResponseEntity<EmployeeDto> create(@Valid @RequestBody EmployeeRequestDto request) {
    EmployeeDto employee = employeeService.create(request);
    return ResponseEntity.created(URI.create("/employees/" + employee.getId())).body(employee);
  }
  /** Updates an employee. */
  @PutMapping("/{id}")
  public EmployeeDto update(@PathVariable UUID id, @Valid @RequestBody EmployeeRequestDto request) { return employeeService.update(id, request); }
  /** Deletes an employee. */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) { employeeService.delete(id); }
}
