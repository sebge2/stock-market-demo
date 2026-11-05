package be.sgerard.demo.employee.controller;

import be.sgerard.demo.employee.config.OpenApiConfiguration;
import be.sgerard.demo.employee.model.dto.DepartmentDto;
import be.sgerard.demo.employee.model.dto.DepartmentRequestDto;
import be.sgerard.demo.employee.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name = "Departments", description = "Management of departments")
@SecurityRequirement(name = OpenApiConfiguration.OAUTH2_SCHEME)
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
  @Operation(summary = "List the departments", description = "Requires the DEPARTMENT_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The departments"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the DEPARTMENT_LIST permission")
  })
  @PreAuthorize("hasAuthority('DEPARTMENT_LIST')")
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
  @Operation(summary = "Get a department by identifier", description = "Requires the DEPARTMENT_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The department"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the DEPARTMENT_LIST permission"),
      @ApiResponse(responseCode = "404", description = "Department not found")
  })
  @PreAuthorize("hasAuthority('DEPARTMENT_LIST')")
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
  @Operation(summary = "Create a department", description = "Requires the DEPARTMENT_CREATE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Department created"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the DEPARTMENT_CREATE permission"),
      @ApiResponse(responseCode = "404", description = "Manager not found"),
      @ApiResponse(responseCode = "409", description = "Department already exists")
  })
  @PreAuthorize("hasAuthority('DEPARTMENT_CREATE')")
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
  @Operation(summary = "Update a department", description = "Requires the DEPARTMENT_UPDATE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Department updated"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the DEPARTMENT_UPDATE permission"),
      @ApiResponse(responseCode = "404", description = "Department or manager not found"),
      @ApiResponse(responseCode = "409", description = "Department already exists")
  })
  @PreAuthorize("hasAuthority('DEPARTMENT_UPDATE')")
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
  @Operation(summary = "Delete a department", description = "Requires the DEPARTMENT_DELETE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Department deleted"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the DEPARTMENT_DELETE permission"),
      @ApiResponse(responseCode = "404", description = "Department not found"),
      @ApiResponse(responseCode = "409", description = "Department still referenced")
  })
  @PreAuthorize("hasAuthority('DEPARTMENT_DELETE')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    departmentService.delete(id);
  }
}
