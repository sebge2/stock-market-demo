package be.sgerard.demo.exchange.staff.mapper;

import be.sgerard.demo.exchange.staff.model.StaffRole;
import be.sgerard.demo.exchange.staff.model.dto.StaffRoleDto;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between StaffRole entity and StaffRoleDto.
 *
 * This mapper handles the conversion of staff role entities to DTOs
 * and vice versa, maintaining separation of concerns between the
 * domain model and API layer.
 */
@Component
public class StaffRoleMapper {

	/**
	 * Converts a StaffRole entity to a StaffRoleDto.
	 *
	 * @param staffRole the staff role entity
	 * @return the staff role DTO
	 */
	public StaffRoleDto toDto(final StaffRole staffRole) {
		if (staffRole == null) {
			return null;
		}

		final StaffRoleDto dto = new StaffRoleDto();
		dto.setId(staffRole.getId());
		dto.setName(staffRole.getName());
		dto.setDescription(staffRole.getDescription());
		dto.setStatus(staffRole.getStatus());
		dto.setCreatedAt(staffRole.getCreatedAt());
		dto.setUpdatedAt(staffRole.getUpdatedAt());

		return dto;
	}

	/**
	 * Converts a StaffRoleDto to a StaffRole entity.
	 *
	 * @param dto the staff role DTO
	 * @return the staff role entity
	 */
	public StaffRole toEntity(final StaffRoleDto dto) {
		if (dto == null) {
			return null;
		}

		return new StaffRole(
			dto.getName(),
			dto.getDescription(),
			dto.getStatus()
		);
	}
}
