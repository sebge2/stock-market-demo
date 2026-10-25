package be.sgerard.demo.user.controller;

import be.sgerard.demo.user.model.dto.RoleDto;
import be.sgerard.demo.user.model.dto.RoleRequestDto;
import be.sgerard.demo.user.service.RoleService;
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
 * Exposes CRUD endpoints for roles and their permissions.
 *
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

  /** Role operations. */
  private final RoleService roleService;

  /** Creates the controller. */
  public RoleController(RoleService roleService) { this.roleService = roleService; }
  /** @return all roles */
  @GetMapping
  public List<RoleDto> findAll() { return roleService.findAll(); }
  /** @param id role identifier @return the role */
  @GetMapping("/{id}")
  public RoleDto findById(@PathVariable UUID id) { return roleService.findById(id); }
  /** @param request creation data @return the created role */
  @PostMapping
  public ResponseEntity<RoleDto> create(@Valid @RequestBody RoleRequestDto request) {
    RoleDto role = roleService.create(request);
    return ResponseEntity.created(URI.create("/roles/" + role.getId())).body(role);
  }
  /** @param id role identifier @param request replacement data @return the updated role */
  @PutMapping("/{id}")
  public RoleDto update(@PathVariable UUID id, @Valid @RequestBody RoleRequestDto request) { return roleService.update(id, request); }
  /** @param id role identifier */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) { roleService.delete(id); }
}
