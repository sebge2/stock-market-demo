package be.sgerard.demo.company.controller;

import be.sgerard.demo.company.model.Company;
import be.sgerard.demo.company.model.CompanyStatus;
import be.sgerard.demo.company.model.SectorType;
import be.sgerard.demo.company.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security integration tests for CompanyController.
 *
 * Verifies that endpoints are properly secured with OAuth2 authentication
 * and authorization checks.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CompanyController Security Tests")
class CompanyControllerSecurityTest {

	/**
	 * Mock MVC for testing HTTP requests.
	 */
	@Autowired
	private MockMvc mockMvc;

	/**
	 * Company repository.
	 */
	@Autowired
	private CompanyRepository companyRepository;

	/**
	 * Setup test data before each test.
	 */
	@BeforeEach
	void setUp() {
		companyRepository.deleteAll();
	}

	/**
	 * Test that unauthenticated requests to protected endpoints are rejected.
	 */
	@Test
	@DisplayName("Unauthenticated request should return 401")
	void testUnauthenticatedRequest() throws Exception {
		mockMvc.perform(get("/companies"))
			.andExpect(status().isUnauthorized());
	}

	/**
	 * Test that users with the COMPANY_LIST authority can read companies.
	 */
	@Test
	@DisplayName("User with COMPANY_LIST authority should access GET endpoints")
	@WithMockUser(authorities = "COMPANY_LIST")
	void testAuthenticatedUserCanReadCompanies() throws Exception {
		final Company company = new Company("Test Company", "US", SectorType.INFORMATION_TECHNOLOGY, CompanyStatus.LISTED);
		companyRepository.save(company);

		mockMvc.perform(get("/companies")
			.with(jwt().authorities(() -> "COMPANY_LIST")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray());
	}

	/**
	 * Test that users without the COMPANY_CREATE authority cannot create companies.
	 */
	@Test
	@DisplayName("User without COMPANY_CREATE authority should not be able to create company")
	@WithMockUser(authorities = "COMPANY_LIST")
	void testUserCannotCreateCompany() throws Exception {
		mockMvc.perform(post("/companies")
			.with(jwt().authorities(() -> "COMPANY_LIST"))
			.contentType("application/json")
			.content("{\"name\":\"Test\",\"countryCode\":\"US\",\"sectorType\":\"INFORMATION_TECHNOLOGY\",\"status\":\"LISTED\"}"))
			.andExpect(status().isForbidden());
	}

	/**
	 * Test that users with the COMPANY_CREATE authority can create companies.
	 */
	@Test
	@DisplayName("User with COMPANY_CREATE authority should be able to create company")
	@WithMockUser(authorities = "COMPANY_CREATE")
	void testAdminCanCreateCompany() throws Exception {
		mockMvc.perform(post("/companies")
			.with(jwt().authorities(() -> "COMPANY_CREATE"))
			.contentType("application/json")
			.content("{\"name\":\"Test Company\",\"countryCode\":\"US\",\"sectorType\":\"INFORMATION_TECHNOLOGY\",\"status\":\"LISTED\"}"))
			.andExpect(status().isCreated());
	}

	/**
	 * Test that public endpoints like Swagger UI are accessible without authentication.
	 */
	@Test
	@DisplayName("Swagger UI should be accessible without authentication")
	void testSwaggerUIIsPublic() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
			.andExpect(status().isFound());
	}

	/**
	 * Test that health actuator endpoint is accessible without authentication.
	 */
	@Test
	@DisplayName("Health actuator endpoint should be accessible without authentication")
	void testHealthEndpointIsPublic() throws Exception {
		mockMvc.perform(get("/actuator/health"))
			.andExpect(status().isOk());
	}
}
