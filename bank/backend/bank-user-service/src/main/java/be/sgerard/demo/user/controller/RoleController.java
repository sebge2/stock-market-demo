package be.sgerard.demo.user.controller;

import be.sgerard.demo.user.config.OpenApiConfiguration;
import be.sgerard.demo.user.model.dto.RoleDto;
import be.sgerard.demo.user.model.dto.RoleRequestDto;
import be.sgerard.demo.user.service.RoleService;
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
 * Exposes CRUD endpoints for roles and their permissions.
 *
 */
@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "Management of roles")
@SecurityRequirement(name = OpenApiConfiguration.OAUTH2_SCHEME)
public class RoleController {

    /**
     * Role operations.
     */
    private final RoleService roleService;

    /**
     * Creates the controller.
     */
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * @return all roles
     */
    @GetMapping
    @Operation(summary = "List the roles", description = "Requires the ROLE_LIST permission.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The list of roles"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Missing the ROLE_LIST permission")
    })
    @PreAuthorize("hasAuthority('ROLE_LIST')")
    public List<RoleDto> findAll() {
        return roleService.findAll();
    }

    /**
     * @param id role identifier @return the role
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a role by identifier", description = "Requires the ROLE_LIST permission.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The role"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Missing the ROLE_LIST permission"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @PreAuthorize("hasAuthority('ROLE_LIST')")
    public RoleDto findById(@PathVariable UUID id) {
        return roleService.findById(id);
    }

    /**
     * @param request creation data @return the created role
     */
    @PostMapping
    @Operation(summary = "Create a role", description = "Requires the ROLE_CREATE permission.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Role created"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Missing the ROLE_CREATE permission"),
        @ApiResponse(responseCode = "404", description = "A referenced permission was not found"),
        @ApiResponse(responseCode = "409", description = "Role already exists")
    })
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<RoleDto> create(@Valid @RequestBody RoleRequestDto request) {
        RoleDto role = roleService.create(request);
        return ResponseEntity.created(URI.create("/roles/" + role.getId())).body(role);
    }

    /**
     * @param id role identifier @param request replacement data @return the updated role
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a role", description = "Requires the ROLE_UPDATE permission.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Missing the ROLE_UPDATE permission"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "409", description = "Role already exists")
    })
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public RoleDto update(@PathVariable UUID id, @Valid @RequestBody RoleRequestDto request) {
        return roleService.update(id, request);
    }

    /**
     * @param id role identifier
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role", description = "Requires the ROLE_DELETE permission.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Role deleted"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Missing the ROLE_DELETE permission"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        roleService.delete(id);
    }
}
