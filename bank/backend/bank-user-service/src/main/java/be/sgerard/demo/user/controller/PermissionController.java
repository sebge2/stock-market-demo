package be.sgerard.demo.user.controller;

import be.sgerard.demo.user.model.dto.PermissionDto;
import be.sgerard.demo.user.model.dto.PermissionRequestDto;
import be.sgerard.demo.user.service.PermissionService;
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
 * Exposes CRUD endpoints for permissions.
 *
 */
@RestController
@RequestMapping("/permissions")
public class PermissionController {

  /** Permission operations. */
  private final PermissionService permissionService;

  /** Creates the controller. */
  public PermissionController(PermissionService permissionService) { this.permissionService = permissionService; }
  /** @return all permissions */
  @GetMapping
  public List<PermissionDto> findAll() { return permissionService.findAll(); }
  /** @param id permission identifier @return the permission */
  @GetMapping("/{id}")
  public PermissionDto findById(@PathVariable UUID id) { return permissionService.findById(id); }
  /** @param request creation data @return the created permission */
  @PostMapping
  public ResponseEntity<PermissionDto> create(@Valid @RequestBody PermissionRequestDto request) {
    PermissionDto permission = permissionService.create(request);
    return ResponseEntity.created(URI.create("/permissions/" + permission.getId())).body(permission);
  }
  /** @param id permission identifier @param request replacement data @return the updated permission */
  @PutMapping("/{id}")
  public PermissionDto update(@PathVariable UUID id, @Valid @RequestBody PermissionRequestDto request) { return permissionService.update(id, request); }
  /** @param id permission identifier */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) { permissionService.delete(id); }
}
