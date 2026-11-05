package be.sgerard.demo.employee.controller;

import be.sgerard.demo.employee.config.OpenApiConfiguration;
import be.sgerard.demo.employee.model.dto.EmployeeDto;
import be.sgerard.demo.employee.model.dto.EmployeeRequestDto;
import be.sgerard.demo.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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

/**
 *
 * Exposes CRUD endpoints for employees.
 *
 */
@RestController
@RequestMapping("/employees")
@Tag(name = "Employees", description = "Management of employees")
@SecurityRequirement(name = OpenApiConfiguration.OAUTH2_SCHEME)
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
  @Operation(summary = "List the employees", description = "Requires the EMPLOYEE_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "A page of employees"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the EMPLOYEE_LIST permission")
  })
  @PreAuthorize("hasAuthority('EMPLOYEE_LIST')")
  @GetMapping
  public Page<EmployeeDto> findAll(@RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
    return employeeService.findAll(pageable);
  }
  /** Returns an employee. */
  @Operation(summary = "Get an employee by identifier", description = "Requires the EMPLOYEE_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The employee"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the EMPLOYEE_LIST permission"),
      @ApiResponse(responseCode = "404", description = "Employee not found")
  })
  @PreAuthorize("hasAuthority('EMPLOYEE_LIST')")
  @GetMapping("/{id}")
  public EmployeeDto findById(@PathVariable UUID id) { return employeeService.findById(id); }
  /**
   *
   * Creates an employee and automatically creates the corresponding user in the user service.
   *
   */
  @Operation(summary = "Create an employee",
      description = "Requires the EMPLOYEE_CREATE permission. The caller's token is forwarded to the "
          + "user service, so the caller also needs the USER_CREATE permission. A user is created "
          + "automatically (username derived from the email, temporary password) and its identifier "
          + "is returned as userId. Nothing is persisted if the user cannot be created.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Employee created"),
      @ApiResponse(responseCode = "400", description = "Invalid request or user creation validation failed"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the EMPLOYEE_CREATE permission"),
      @ApiResponse(responseCode = "404", description = "Department not found"),
      @ApiResponse(responseCode = "409", description = "Employee or username already exists"),
      @ApiResponse(responseCode = "500", description = "Unexpected error while creating the user"),
      @ApiResponse(responseCode = "502", description = "User service failed or refused the request"),
      @ApiResponse(responseCode = "503", description = "User service unreachable after retries")
  })
  @PreAuthorize("hasAuthority('EMPLOYEE_CREATE')")
  @PostMapping
  public ResponseEntity<EmployeeDto> create(@Valid @RequestBody EmployeeRequestDto request) {
    EmployeeDto employee = employeeService.create(request);
    return ResponseEntity.created(URI.create("/employees/" + employee.getId())).body(employee);
  }
  /** Updates an employee. */
  @Operation(summary = "Update an employee", description = "Requires the EMPLOYEE_UPDATE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Employee updated"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the EMPLOYEE_UPDATE permission"),
      @ApiResponse(responseCode = "404", description = "Employee or department not found"),
      @ApiResponse(responseCode = "409", description = "Employee already exists")
  })
  @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
  @PutMapping("/{id}")
  public EmployeeDto update(@PathVariable UUID id, @Valid @RequestBody EmployeeRequestDto request) { return employeeService.update(id, request); }
  /** Deletes an employee. */
  @Operation(summary = "Delete an employee", description = "Requires the EMPLOYEE_DELETE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Employee deleted"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the EMPLOYEE_DELETE permission"),
      @ApiResponse(responseCode = "404", description = "Employee not found")
  })
  @PreAuthorize("hasAuthority('EMPLOYEE_DELETE')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) { employeeService.delete(id); }
}
