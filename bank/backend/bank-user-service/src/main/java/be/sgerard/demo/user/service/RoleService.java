package be.sgerard.demo.user.service;

import be.sgerard.demo.user.mapper.RoleMapper;
import be.sgerard.demo.user.model.Permission;
import be.sgerard.demo.user.model.Role;
import be.sgerard.demo.user.model.dto.RoleDto;
import be.sgerard.demo.user.model.dto.RoleRequestDto;
import be.sgerard.demo.user.repository.PermissionRepository;
import be.sgerard.demo.user.repository.RoleRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Provides CRUD operations for roles and their permissions.
 *
 */
@Service
@Transactional(readOnly = true)
public class RoleService {

  /** Role persistence. */
  private final RoleRepository roleRepository;
  /** Permission persistence. */
  private final PermissionRepository permissionRepository;
  /** API mapper. */
  private final RoleMapper roleMapper;

  /** Creates the service. */
  public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository,
      RoleMapper roleMapper) {
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
    this.roleMapper = roleMapper;
  }

  /** @return all roles */
  public List<RoleDto> findAll() { return roleRepository.findAll().stream().map(roleMapper::toDto).toList(); }
  /** @param id role identifier @return the role */
  public RoleDto findById(UUID id) { return roleMapper.toDto(findEntity(id)); }

  /** Creates a role. */
  @Transactional
  public RoleDto create(RoleRequestDto request) {
    roleRepository.findByName(request.getName()).ifPresent(role -> { throw conflict(request.getName()); });
    Role role = roleMapper.toEntity(request);
    replacePermissions(role, request.getPermissionIds());
    return roleMapper.toDto(roleRepository.save(role));
  }

  /** Updates a role. */
  @Transactional
  public RoleDto update(UUID id, RoleRequestDto request) {
    Role role = findEntity(id);
    roleRepository.findByName(request.getName()).filter(existing -> !existing.getId().equals(id))
        .ifPresent(existing -> { throw conflict(request.getName()); });
    roleMapper.updateEntity(request, role);
    replacePermissions(role, request.getPermissionIds());
    return roleMapper.toDto(roleRepository.save(role));
  }

  /** Deletes a role. */
  @Transactional
  public void delete(UUID id) {
    roleRepository.delete(findEntity(id));
  }

  /** Replaces a role's permissions with the requested set. */
  private void replacePermissions(Role role, Set<UUID> permissionIds) {
    Set<Permission> permissions = new LinkedHashSet<>();
    for (UUID permissionId : permissionIds) {
      permissions.add(permissionRepository.findById(permissionId).orElseThrow(
          () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not found: " + permissionId)));
    }
    new LinkedHashSet<>(role.getPermissions()).forEach(role::removePermission);
    permissions.forEach(role::addPermission);
  }

  /** Finds an entity or raises a not-found response. */
  private Role findEntity(UUID id) { return roleRepository.findById(id).orElseThrow(() -> notFound(id)); }
  /** Builds a not-found response. */
  private ResponseStatusException notFound(UUID id) { return new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + id); }
  /** Builds a duplicate-name response. */
  private ResponseStatusException conflict(String name) { return new ResponseStatusException(HttpStatus.CONFLICT, "Role name already exists: " + name); }
}
