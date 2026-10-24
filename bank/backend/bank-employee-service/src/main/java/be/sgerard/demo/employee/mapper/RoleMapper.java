package be.sgerard.demo.employee.mapper;

import be.sgerard.demo.employee.model.Role;
import be.sgerard.demo.employee.model.dto.RoleDto;
import be.sgerard.demo.employee.model.dto.RoleRequestDto;
import org.springframework.stereotype.Component;

/**
 *
 * Maps role entities to their API representations.
 *
 */
@Component
public class RoleMapper {

  /** Creates an entity from a request. */
  public Role toEntity(RoleRequestDto request) {
    return new Role(request.getName(), request.getDescription(), request.getStatus());
  }

  /** Copies mutable request data to an entity. */
  public void updateEntity(RoleRequestDto request, Role role) {
    role.setName(request.getName());
    role.setDescription(request.getDescription());
    role.setStatus(request.getStatus());
  }

  /** Converts an entity to a response. */
  public RoleDto toDto(Role role) {
    RoleDto dto = new RoleDto();
    dto.setId(role.getId());
    dto.setName(role.getName());
    dto.setDescription(role.getDescription());
    dto.setStatus(role.getStatus());
    dto.setPermissionIds(role.getPermissions().stream().map(permission -> permission.getId()).collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new)));
    dto.setCreatedAt(role.getCreatedAt());
    dto.setUpdatedAt(role.getUpdatedAt());
    return dto;
  }
}
