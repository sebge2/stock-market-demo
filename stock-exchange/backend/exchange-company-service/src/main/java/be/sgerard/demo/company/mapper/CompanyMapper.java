package be.sgerard.demo.company.mapper;

import be.sgerard.demo.company.model.Company;
import be.sgerard.demo.company.model.dto.CompanyDto;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Company entity and CompanyDto.
 *
 * This mapper provides methods for converting Company entities to DTOs
 * and vice versa.
 */
@Component
public class CompanyMapper {

	/**
	 * Converts a Company entity to a CompanyDto.
	 *
	 * @param company the company entity to convert
	 * @return the company DTO
	 */
	public CompanyDto toDto(final Company company) {
		if (company == null) {
			return null;
		}

		final CompanyDto dto = new CompanyDto();
		dto.setId(company.getId());
		dto.setName(company.getName());
		dto.setCountryCode(company.getCountryCode());
		dto.setWebsite(company.getWebsite());
		dto.setLogoUrl(company.getLogoUrl());
		dto.setSectorType(company.getSectorType());
		dto.setStatus(company.getStatus());
		dto.setCreatedAt(company.getCreatedAt());
		dto.setUpdatedAt(company.getUpdatedAt());

		return dto;
	}

	/**
	 * Converts a CompanyDto to a Company entity.
	 *
	 * @param dto the company DTO to convert
	 * @return the company entity
	 */
	public Company toEntity(final CompanyDto dto) {
		if (dto == null) {
			return null;
		}

		final Company company = new Company(dto.getName(), dto.getCountryCode(), dto.getSectorType(), dto.getStatus());
		company.setWebsite(dto.getWebsite());
		company.setLogoUrl(dto.getLogoUrl());
		company.setSectorType(dto.getSectorType());

		return company;
	}
}
