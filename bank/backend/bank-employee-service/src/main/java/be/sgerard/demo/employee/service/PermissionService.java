package be.sgerard.demo.employee.service;

import be.sgerard.demo.employee.mapper.PermissionMapper;
import be.sgerard.demo.employee.model.Permission;
import be.sgerard.demo.employee.model.dto.PermissionDto;
import be.sgerard.demo.employee.model.dto.PermissionRequestDto;
import be.sgerard.demo.employee.repository.PermissionRepository;
import be.sgerard.demo.employee.repository.RoleRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Provides CRUD operations for permissions.
 *
 */
@Service
@Transactional(readOnly = true)
public class PermissionService {

  /** Permission persistence. */
  private final PermissionRepository permissionRepository;
  /** Role persistence used to protect assigned permissions. */
  private final RoleRepository roleRepository;
  /** API mapper. */
  private final PermissionMapper permissionMapper;

  /** Creates the service. */
  public PermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository, PermissionMapper permissionMapper) {
    this.permissionRepository = permissionRepository;
    this.roleRepository = roleRepository;
    this.permissionMapper = permissionMapper;
  }

  /** @return all permissions */
  public List<PermissionDto> findAll() { return permissionRepository.findAll().stream().map(permissionMapper::toDto).toList(); }
  /** @param id permission identifier @return the permission */
  public PermissionDto findById(UUID id) { return permissionMapper.toDto(findEntity(id)); }

  /** Creates a permission. */
  @Transactional
  public PermissionDto create(PermissionRequestDto request) {
    permissionRepository.findByCode(request.getCode()).ifPresent(permission -> { throw conflict(request.getCode()); });
    return permissionMapper.toDto(permissionRepository.save(permissionMapper.toEntity(request)));
  }

  /** Updates a permission. */
  @Transactional
  public PermissionDto update(UUID id, PermissionRequestDto request) {
    Permission permission = findEntity(id);
    permissionRepository.findByCode(request.getCode())
        .filter(existing -> !existing.getId().equals(id))
        .ifPresent(existing -> { throw conflict(request.getCode()); });
    permissionMapper.updateEntity(request, permission);
    return permissionMapper.toDto(permissionRepository.save(permission));
  }

  /** Deletes an unassigned permission. */
  @Transactional
  public void delete(UUID id) {
    if (roleRepository.existsByPermissionsId(id)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Permission is assigned to a role: " + id);
    }
    permissionRepository.delete(findEntity(id));
  }

  /** Finds an entity or raises a not-found response. */
  private Permission findEntity(UUID id) { return permissionRepository.findById(id).orElseThrow(() -> notFound(id)); }
  /** Builds a not-found response. */
  private ResponseStatusException notFound(UUID id) { return new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not found: " + id); }
  /** Builds a duplicate-code response. */
  private ResponseStatusException conflict(String code) { return new ResponseStatusException(HttpStatus.CONFLICT, "Permission code already exists: " + code); }
}
