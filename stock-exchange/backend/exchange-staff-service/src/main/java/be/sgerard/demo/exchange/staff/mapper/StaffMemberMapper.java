package be.sgerard.demo.exchange.staff.mapper;

import be.sgerard.demo.exchange.staff.model.StaffMember;
import be.sgerard.demo.exchange.staff.model.dto.StaffMemberDto;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between StaffMember entity and StaffMemberDto.
 *
 * This mapper handles the conversion of staff member entities to DTOs
 * and vice versa, maintaining separation of concerns between the
 * domain model and API layer.
 */
@Component
public class StaffMemberMapper {

	/**
	 * Converts a StaffMember entity to a StaffMemberDto.
	 *
	 * @param staffMember the staff member entity
	 * @return the staff member DTO
	 */
	public StaffMemberDto toDto(final StaffMember staffMember) {
		if (staffMember == null) {
			return null;
		}

		final StaffMemberDto dto = new StaffMemberDto();
		dto.setId(staffMember.getId());
		dto.setFirstName(staffMember.getFirstName());
		dto.setLastName(staffMember.getLastName());
		dto.setEmail(staffMember.getEmail());
		dto.setStatus(staffMember.getStatus());
		dto.setCreatedAt(staffMember.getCreatedAt());
		dto.setUpdatedAt(staffMember.getUpdatedAt());

		if (staffMember.getStaffRole() != null) {
			dto.setStaffRoleId(staffMember.getStaffRole().getId());
			dto.setStaffRoleName(staffMember.getStaffRole().getName());
		}

		return dto;
	}

	/**
	 * Converts a StaffMemberDto to a StaffMember entity.
	 *
	 * @param dto the staff member DTO
	 * @return the staff member entity
	 */
	public StaffMember toEntity(final StaffMemberDto dto) {
		if (dto == null) {
			return null;
		}

		final StaffMember staffMember = new StaffMember(
			dto.getFirstName(),
			dto.getLastName(),
			dto.getEmail(),
			dto.getStatus(),
			null
		);

		return staffMember;
	}
}
