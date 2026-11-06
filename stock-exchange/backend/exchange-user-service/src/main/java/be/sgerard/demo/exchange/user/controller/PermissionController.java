package be.sgerard.demo.exchange.user.controller;

import be.sgerard.demo.exchange.user.config.OpenApiConfiguration;
import be.sgerard.demo.exchange.user.model.dto.PermissionDto;
import be.sgerard.demo.exchange.user.model.dto.PermissionRequestDto;
import be.sgerard.demo.exchange.user.service.PermissionService;
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
 * Exposes CRUD endpoints for permissions.
 *
 */
@RestController
@RequestMapping("/permissions")
@Tag(name = "Permissions", description = "Management of permissions")
@SecurityRequirement(name = OpenApiConfiguration.OAUTH2_SCHEME)
public class PermissionController {

    /**
     * Permission operations.
     */
    private final PermissionService permissionService;

    /**
     * Creates the controller.
     */
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * @return all permissions
     */
    @GetMapping
    @Operation(summary = "List the permissions", description = "Requires the PERMISSION_LIST permission.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The list of permissions"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Missing the PERMISSION_LIST permission")
    })
    @PreAuthorize("hasAuthority('PERMISSION_LIST')")
    public List<PermissionDto> findAll() {
        return permissionService.findAll();
    }

    /**
     * @param id permission identifier @return the permission
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a permission by identifier", description = "Requires the PERMISSION_LIST permission.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The permission"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Missing the PERMISSION_LIST permission"),
        @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @PreAuthorize("hasAuthority('PERMISSION_LIST')")
    public PermissionDto findById(@PathVariable UUID id) {
        return permissionService.findById(id);
    }

    /**
     * @param request creation data @return the created permission
     */
    @PostMapping
    @Operation(summary = "Create a permission", description = "Requires the PERMISSION_CREATE permission.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Permission created"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Missing the PERMISSION_CREATE permission"),
        @ApiResponse(responseCode = "409", description = "Permission already exists")
    })
    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    public ResponseEntity<PermissionDto> create(@Valid @RequestBody PermissionRequestDto request) {
        PermissionDto permission = permissionService.create(request);
        return ResponseEntity.created(URI.create("/permissions/" + permission.getId())).body(permission);
    }

    /**
     * @param id permission identifier @param request replacement data @return the updated permission
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a permission", description = "Requires the PERMISSION_UPDATE permission.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permission updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Missing the PERMISSION_UPDATE permission"),
        @ApiResponse(responseCode = "404", description = "Permission not found"),
        @ApiResponse(responseCode = "409", description = "Permission already exists")
    })
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")
    public PermissionDto update(@PathVariable UUID id, @Valid @RequestBody PermissionRequestDto request) {
        return permissionService.update(id, request);
    }

    /**
     * @param id permission identifier
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a permission", description = "Requires the PERMISSION_DELETE permission.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Permission deleted"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Missing the PERMISSION_DELETE permission"),
        @ApiResponse(responseCode = "404", description = "Permission not found"),
        @ApiResponse(responseCode = "409", description = "Permission still assigned to a role")
    })
    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        permissionService.delete(id);
    }
}
