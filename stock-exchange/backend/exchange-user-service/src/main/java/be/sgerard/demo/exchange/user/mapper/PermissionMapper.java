package be.sgerard.demo.exchange.user.mapper;

import be.sgerard.demo.exchange.user.model.Permission;
import be.sgerard.demo.exchange.user.model.dto.PermissionDto;
import be.sgerard.demo.exchange.user.model.dto.PermissionRequestDto;
import org.springframework.stereotype.Component;

/**
 *
 * Maps permission entities to their API representations.
 *
 */
@Component
public class PermissionMapper {

  /** Creates an entity from a request. */
  public Permission toEntity(PermissionRequestDto request) {
    return new Permission(request.getCode(), request.getDescription());
  }

  /** Copies mutable request data to an entity. */
  public void updateEntity(PermissionRequestDto request, Permission permission) {
    permission.setCode(request.getCode());
    permission.setDescription(request.getDescription());
  }

  /** Converts an entity to a response. */
  public PermissionDto toDto(Permission permission) {
    PermissionDto dto = new PermissionDto();
    dto.setId(permission.getId());
    dto.setCode(permission.getCode());
    dto.setDescription(permission.getDescription());
    dto.setCreatedAt(permission.getCreatedAt());
    dto.setUpdatedAt(permission.getUpdatedAt());
    return dto;
  }
}
