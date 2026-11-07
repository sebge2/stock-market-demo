package be.sgerard.demo.exchange.staff.controller;

import be.sgerard.demo.exchange.staff.config.OpenApiConfiguration;
import be.sgerard.demo.exchange.staff.mapper.StaffMemberMapper;
import be.sgerard.demo.exchange.staff.model.StaffMember;
import be.sgerard.demo.exchange.staff.model.dto.StaffMemberDto;
import be.sgerard.demo.exchange.staff.repository.StaffMemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * REST controller for managing staff members.
 * <p>
 * This controller provides endpoints for retrieving and managing staff members
 * in the exchange system. All endpoints are secured with OAuth2.
 */
@RestController
@RequestMapping("/staff-members")
@Tag(name = "Staff Members", description = "Manage staff members and their assignments")
@SecurityRequirement(name = OpenApiConfiguration.OAUTH2_SCHEME)
public class StaffMemberController {

    /**
     * Staff member repository.
     */
    private final StaffMemberRepository staffMemberRepository;

    /**
     * Staff member mapper.
     */
    private final StaffMemberMapper staffMemberMapper;

    /**
     * Constructor for dependency injection.
     *
     * @param staffMemberRepository the staff member repository
     * @param staffMemberMapper     the staff member mapper
     */
    public StaffMemberController(final StaffMemberRepository staffMemberRepository, final StaffMemberMapper staffMemberMapper) {
        this.staffMemberRepository = staffMemberRepository;
        this.staffMemberMapper = staffMemberMapper;
    }

    /**
     * Retrieves all staff members.
     *
     * @return a list of all staff members
     */
    @GetMapping
    @PreAuthorize("hasAuthority('STAFF_LIST')")
    @Operation(summary = "Get all staff members", description = "Retrieve a list of all staff members in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of staff members"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    public ResponseEntity<List<StaffMemberDto>> getAllStaffMembers() {
        final List<StaffMember> staffMembers = staffMemberRepository.findAll();
        final List<StaffMemberDto> dtos = staffMembers.stream()
                .map(staffMemberMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Retrieves a staff member by its ID.
     *
     * @param id the staff member ID
     * @return the staff member if found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STAFF_LIST')")
    @Operation(summary = "Get staff member by ID", description = "Retrieve a specific staff member by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved staff member"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Staff member not found")
    })
    public ResponseEntity<StaffMemberDto> getStaffMemberById(
            @Parameter(description = "The staff member ID")
            @PathVariable final UUID id
    ) {
        return staffMemberRepository.findById(id)
                .map(staffMember -> ResponseEntity.ok(staffMemberMapper.toDto(staffMember)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new staff member.
     *
     * @param staffMemberDto the staff member data
     * @return the created staff member
     */
    @PostMapping
    @PreAuthorize("hasAuthority('STAFF_CREATE')")
    @Operation(summary = "Create new staff member", description = "Create a new staff member in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Staff member successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid staff member data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin role required")
    })
    public ResponseEntity<StaffMemberDto> createStaffMember(
            @Parameter(description = "The staff member data")
            @Valid @RequestBody final StaffMemberDto staffMemberDto
    ) {
        final StaffMember staffMember = staffMemberMapper.toEntity(staffMemberDto);
        final StaffMember saved = staffMemberRepository.save(staffMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(staffMemberMapper.toDto(saved));
    }

    /**
     * Updates an existing staff member.
     *
     * @param id             the staff member ID
     * @param staffMemberDto the updated staff member data
     * @return the updated staff member
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STAFF_UPDATE')")
    @Operation(summary = "Update staff member", description = "Update an existing staff member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff member successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid staff member data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin role required"),
            @ApiResponse(responseCode = "404", description = "Staff member not found")
    })
    public ResponseEntity<StaffMemberDto> updateStaffMember(
            @Parameter(description = "The staff member ID")
            @PathVariable final UUID id,
            @Parameter(description = "The updated staff member data")
            @Valid @RequestBody final StaffMemberDto staffMemberDto
    ) {
        return staffMemberRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(staffMemberDto.getFirstName());
                    existing.setLastName(staffMemberDto.getLastName());
                    existing.setEmail(staffMemberDto.getEmail());
                    existing.setStatus(staffMemberDto.getStatus());

                    final StaffMember updated = staffMemberRepository.save(existing);
                    return ResponseEntity.ok(staffMemberMapper.toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a staff member.
     *
     * @param id the staff member ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STAFF_DELETE')")
    @Operation(summary = "Delete staff member", description = "Delete a staff member from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Staff member successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin role required"),
            @ApiResponse(responseCode = "404", description = "Staff member not found")
    })
    public ResponseEntity<Void> deleteStaffMember(
            @Parameter(description = "The staff member ID")
            @PathVariable final UUID id
    ) {
        if (!staffMemberRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        staffMemberRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
