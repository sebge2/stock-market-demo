package be.sgerard.demo.company.controller;

import be.sgerard.demo.company.mapper.CompanyMapper;
import be.sgerard.demo.company.model.Company;
import be.sgerard.demo.company.model.dto.CompanyDto;
import be.sgerard.demo.company.repository.CompanyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing companies.
 *
 * This controller provides endpoints for retrieving and managing companies
 * in the exchange system. All endpoints are secured with OAuth2.
 */
@RestController
@RequestMapping("/companies")
@Tag(name = "Companies", description = "Manage companies and their listings")
@SecurityRequirement(name = "OAuth2")
public class CompanyController {

	/**
	 * Company repository.
	 */
	private final CompanyRepository companyRepository;

	/**
	 * Company mapper.
	 */
	private final CompanyMapper companyMapper;

	/**
	 * Constructor for dependency injection.
	 *
	 * @param companyRepository the company repository
	 * @param companyMapper the company mapper
	 */
	public CompanyController(final CompanyRepository companyRepository, final CompanyMapper companyMapper) {
		this.companyRepository = companyRepository;
		this.companyMapper = companyMapper;
	}

	/**
	 * Retrieves all companies.
	 *
	 * @return a list of all companies
	 */
	@GetMapping
	@PreAuthorize("hasAuthority('COMPANY_LIST')")
	@Operation(summary = "Get all companies", description = "Retrieve a list of all companies in the system")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successfully retrieved list of companies"),
		@ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
		@ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
	})
	public ResponseEntity<List<CompanyDto>> getAllCompanies() {
		final List<Company> companies = companyRepository.findAll();
		final List<CompanyDto> dtos = companies.stream()
			.map(companyMapper::toDto)
			.collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	/**
	 * Retrieves a company by its ID.
	 *
	 * @param id the company ID
	 * @return the company if found
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('COMPANY_LIST')")
	@Operation(summary = "Get company by ID", description = "Retrieve a specific company by its unique identifier")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Successfully retrieved company"),
		@ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
		@ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
		@ApiResponse(responseCode = "404", description = "Company not found")
	})
	public ResponseEntity<CompanyDto> getCompanyById(
		@Parameter(description = "The company ID")
		@PathVariable final UUID id
	) {
		return companyRepository.findById(id)
			.map(company -> ResponseEntity.ok(companyMapper.toDto(company)))
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Creates a new company.
	 *
	 * @param companyDto the company data
	 * @return the created company
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('COMPANY_CREATE')")
	@Operation(summary = "Create new company", description = "Create a new company in the system")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Company successfully created"),
		@ApiResponse(responseCode = "400", description = "Invalid company data"),
		@ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
		@ApiResponse(responseCode = "403", description = "Forbidden - admin role required")
	})
	public ResponseEntity<CompanyDto> createCompany(
		@Parameter(description = "The company data")
		@Valid @RequestBody final CompanyDto companyDto
	) {
		final Company company = companyMapper.toEntity(companyDto);
		final Company saved = companyRepository.save(company);
		return ResponseEntity.status(HttpStatus.CREATED).body(companyMapper.toDto(saved));
	}

	/**
	 * Updates an existing company.
	 *
	 * @param id the company ID
	 * @param companyDto the updated company data
	 * @return the updated company
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('COMPANY_UPDATE')")
	@Operation(summary = "Update company", description = "Update an existing company")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Company successfully updated"),
		@ApiResponse(responseCode = "400", description = "Invalid company data"),
		@ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
		@ApiResponse(responseCode = "403", description = "Forbidden - admin role required"),
		@ApiResponse(responseCode = "404", description = "Company not found")
	})
	public ResponseEntity<CompanyDto> updateCompany(
		@Parameter(description = "The company ID")
		@PathVariable final UUID id,
		@Parameter(description = "The updated company data")
		@Valid @RequestBody final CompanyDto companyDto
	) {
		return companyRepository.findById(id)
			.map(existing -> {
				existing.setName(companyDto.getName());
				existing.setCountryCode(companyDto.getCountryCode());
				existing.setWebsite(companyDto.getWebsite());
				existing.setLogoUrl(companyDto.getLogoUrl());
				existing.setIndustryId(companyDto.getIndustryId());
				existing.setStatus(companyDto.getStatus());

				final Company updated = companyRepository.save(existing);
				return ResponseEntity.ok(companyMapper.toDto(updated));
			})
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Deletes a company.
	 *
	 * @param id the company ID
	 * @return no content response
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('COMPANY_DELETE')")
	@Operation(summary = "Delete company", description = "Delete a company from the system")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Company successfully deleted"),
		@ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
		@ApiResponse(responseCode = "403", description = "Forbidden - admin role required"),
		@ApiResponse(responseCode = "404", description = "Company not found")
	})
	public ResponseEntity<Void> deleteCompany(
		@Parameter(description = "The company ID")
		@PathVariable final UUID id
	) {
		if (!companyRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}

		companyRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
