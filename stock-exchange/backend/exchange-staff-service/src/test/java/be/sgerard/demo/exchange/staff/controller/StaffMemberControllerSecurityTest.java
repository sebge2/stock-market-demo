package be.sgerard.demo.exchange.staff.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security integration tests for StaffMemberController.
 *
 * Tests that verify OAuth2 authentication and authorization is properly
 * configured for the staff member endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class StaffMemberControllerSecurityTest {

	/**
	 * Mock MVC for testing REST endpoints.
	 */
	@Autowired
	private MockMvc mockMvc;

	/**
	 * Test that unauthenticated requests to /staff-members are denied.
	 */
	@Test
	public void testUnauthorizedAccessToStaffMembers() throws Exception {
		mockMvc.perform(get("/staff-members"))
			.andExpect(status().isUnauthorized());
	}

	/**
	 * Test that authenticated USER role can read staff members.
	 */
	@Test
	@WithMockUser(roles = "USER")
	public void testAuthorizedUserCanReadStaffMembers() throws Exception {
		mockMvc.perform(get("/staff-members"))
			.andExpect(status().isOk());
	}

	/**
	 * Test that authenticated ADMIN role can read staff members.
	 */
	@Test
	@WithMockUser(roles = "ADMIN")
	public void testAuthorizedAdminCanReadStaffMembers() throws Exception {
		mockMvc.perform(get("/staff-members"))
			.andExpect(status().isOk());
	}

	/**
	 * Test that only ADMIN role can create staff members.
	 */
	@Test
	@WithMockUser(roles = "USER")
	public void testUserCannotCreateStaffMembers() throws Exception {
		final String createPayload = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"status\":\"ACTIVE\"}";
		mockMvc.perform(post("/staff-members")
			.contentType("application/json")
			.content(createPayload))
			.andExpect(status().isForbidden());
	}

	/**
	 * Test that ADMIN role can create staff members.
	 */
	@Test
	@WithMockUser(roles = "ADMIN")
	public void testAdminCanCreateStaffMembers() throws Exception {
		final String createPayload = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"status\":\"ACTIVE\"}";
		mockMvc.perform(post("/staff-members")
			.contentType("application/json")
			.content(createPayload))
			.andExpect(status().isCreated());
	}

	/**
	 * Test that swagger-ui is publicly accessible.
	 */
	@Test
	public void testSwaggerUiIsPubliclyAccessible() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
			.andExpect(status().isOk());
	}

	/**
	 * Test that actuator health is publicly accessible.
	 */
	@Test
	public void testActuatorHealthIsPubliclyAccessible() throws Exception {
		mockMvc.perform(get("/actuator/health"))
			.andExpect(status().isOk());
	}

	/**
	 * Test that OpenAPI docs are publicly accessible.
	 */
	@Test
	public void testOpenApiDocsArePubliclyAccessible() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
			.andExpect(status().isOk());
	}
}
