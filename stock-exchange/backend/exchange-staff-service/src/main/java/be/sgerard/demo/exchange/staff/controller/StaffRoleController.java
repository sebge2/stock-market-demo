package be.sgerard.demo.exchange.staff.controller;

import be.sgerard.demo.exchange.staff.mapper.StaffRoleMapper;
import be.sgerard.demo.exchange.staff.model.StaffRole;
import be.sgerard.demo.exchange.staff.model.dto.StaffRoleDto;
import be.sgerard.demo.exchange.staff.repository.StaffRoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing staff roles.
 *
 * This controller provides endpoints for retrieving and managing staff roles
 * in the exchange system. All endpoints are secured with OAuth2.
 */
@RestController
@RequestMapping("/staff-roles")
@Tag(name = "Staff Roles", description = "Manage staff roles and assignments")
@SecurityRequirement(name = "OAuth2")
public class StaffRoleController {

	/**
	 * Staff role repository.
	 */
	private final StaffRoleRepository staffRoleRepository;

	/**
	 * Staff role mapper.
	 */
	private final StaffRoleMapper staffRoleMapper;

	/**
	 * Constructor for dependency injection.
	 *
	 * @param staffRoleRepository the staff role repository
	 * @param staffRoleMapper the staff role mapper
	 */
	public StaffRoleController(final StaffRoleRepository staffRoleRepository, final StaffRoleMapper staffRoleMapper) {
		this.staffRoleRepository = staffRoleRepository;
		this.staffRoleMapper = staffRoleMapper;
	}

	/**
	 * Retrieves all staff roles.
	 *
	 * @return a list of all staff roles
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@Operation(summary = "Get all staff roles", description = "Retrieve a list of all staff roles in the system")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successfully retrieved list of staff roles"),
		@ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
		@ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
	})
	public ResponseEntity<List<StaffRoleDto>> getAllStaffRoles() {
		final List<StaffRole> staffRoles = staffRoleRepository.findAll();
		final List<StaffRoleDto> dtos = staffRoles.stream()
			.map(staffRoleMapper::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	/**
	 * Retrieves a staff role by its ID.
	 *
	 * @param id the staff role ID
	 * @return the staff role if found
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@Operation(summary = "Get staff role by ID", description = "Retrieve a specific staff role by its unique identifier")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successfully retrieved staff role"),
		@ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
		@ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
		@ApiResponse(responseCode = "404", description = "Staff role not found")
	})
	public ResponseEntity<StaffRoleDto> getStaffRoleById(
		@Parameter(description = "The staff role ID")
		@PathVariable final UUID id
	) {
		return staffRoleRepository.findById(id)
			.map(staffRole -> ResponseEntity.ok(staffRoleMapper.toDto(staffRole)))
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Creates a new staff role.
	 *
	 * @param staffRoleDto the staff role data
	 * @return the created staff role
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Create new staff role", description = "Create a new staff role in the system")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Staff role successfully created"),
		@ApiResponse(responseCode = "400", description = "Invalid staff role data"),
		@ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
		@ApiResponse(responseCode = "403", description = "Forbidden - admin role required")
	})
	public ResponseEntity<StaffRoleDto> createStaffRole(
		@Parameter(description = "The staff role data")
		@Valid @RequestBody final StaffRoleDto staffRoleDto
	) {
		final StaffRole staffRole = staffRoleMapper.toEntity(staffRoleDto);
		final StaffRole saved = staffRoleRepository.save(staffRole);
		return ResponseEntity.status(HttpStatus.CREATED).body(staffRoleMapper.toDto(saved));
	}

	/**
	 * Updates an existing staff role.
	 *
	 * @param id the staff role ID
	 * @param staffRoleDto the updated staff role data
	 * @return the updated staff role
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update staff role", description = "Update an existing staff role")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Staff role successfully updated"),
		@ApiResponse(responseCode = "400", description = "Invalid staff role data"),
		@ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
		@ApiResponse(responseCode = "403", description = "Forbidden - admin role required"),
		@ApiResponse(responseCode = "404", description = "Staff role not found")
	})
	public ResponseEntity<StaffRoleDto> updateStaffRole(
		@Parameter(description = "The staff role ID")
		@PathVariable final UUID id,
		@Parameter(description = "The updated staff role data")
		@Valid @RequestBody final StaffRoleDto staffRoleDto
	) {
		return staffRoleRepository.findById(id)
			.map(existing -> {
				existing.setName(staffRoleDto.getName());
				existing.setDescription(staffRoleDto.getDescription());
				existing.setStatus(staffRoleDto.getStatus());

				final StaffRole updated = staffRoleRepository.save(existing);
				return ResponseEntity.ok(staffRoleMapper.toDto(updated));
			})
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Deletes a staff role.
	 *
	 * @param id the staff role ID
	 * @return no content response
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete staff role", description = "Delete a staff role from the system")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Staff role successfully deleted"),
		@ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
		@ApiResponse(responseCode = "403", description = "Forbidden - admin role required"),
		@ApiResponse(responseCode = "404", description = "Staff role not found")
	})
	public ResponseEntity<Void> deleteStaffRole(
		@Parameter(description = "The staff role ID")
		@PathVariable final UUID id
	) {
		if (!staffRoleRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}

		staffRoleRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
