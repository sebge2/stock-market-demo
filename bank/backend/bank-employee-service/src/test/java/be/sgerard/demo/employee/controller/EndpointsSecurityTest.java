package be.sgerard.demo.employee.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 *
 * Verifies that the employee and department endpoints are secured by permissions and documented.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class EndpointsSecurityTest {

  /**
   *
   * Mock MVC entry point.
   *
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   *
   * Anonymous callers are rejected.
   *
   * @throws Exception on failure
   */
  @Test
  void anonymousIsUnauthorized() throws Exception {
    mockMvc.perform(get("/employees")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/departments")).andExpect(status().isUnauthorized());
  }

  /**
   *
   * Callers without the matching permission are forbidden.
   *
   * @throws Exception on failure
   */
  @Test
  void withoutMatchingPermissionIsForbidden() throws Exception {
    final UUID id = UUID.randomUUID();

    mockMvc.perform(get("/employees").with(authority("SCOPE_employee.read"))).andExpect(status().isForbidden());
    mockMvc.perform(get("/employees").with(authority("DEPARTMENT_LIST"))).andExpect(status().isForbidden());
    mockMvc.perform(get("/employees/" + id).with(authority("EMPLOYEE_CREATE"))).andExpect(status().isForbidden());
    mockMvc.perform(post("/employees").with(authority("EMPLOYEE_LIST")).contentType("application/json")
        .content("{\"firstName\":\"A\",\"lastName\":\"B\",\"email\":\"a@b.c\",\"status\":\"ACTIVE\"}")).andExpect(status().isForbidden());
    mockMvc.perform(delete("/employees/" + id).with(authority("EMPLOYEE_LIST"))).andExpect(status().isForbidden());
    mockMvc.perform(get("/departments").with(authority("EMPLOYEE_LIST"))).andExpect(status().isForbidden());
    mockMvc.perform(delete("/departments/" + id).with(authority("DEPARTMENT_LIST")))
        .andExpect(status().isForbidden());
  }

  /**
   *
   * Callers with the matching permission are allowed.
   *
   * @throws Exception on failure
   */
  @Test
  void withMatchingPermissionIsOk() throws Exception {
    mockMvc.perform(get("/employees").with(authority("EMPLOYEE_LIST"))).andExpect(status().isOk());
    mockMvc.perform(get("/departments").with(authority("DEPARTMENT_LIST"))).andExpect(status().isOk());
  }

  /**
   *
   * The API documentation is public and declares the OAuth2 flow of the user service.
   *
   * @throws Exception on failure
   */
  @Test
  void documentsOAuth2Flow() throws Exception {
    mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
        .andExpect(jsonPath("$.components.securitySchemes.oauth2.flows.authorizationCode.authorizationUrl")
            .value("http://localhost:9000/oauth2/authorize"))
        .andExpect(jsonPath("$.components.schemas.Employee").exists())
        .andExpect(jsonPath("$.components.schemas.EmployeeDto").doesNotExist());
  }

  /**
   *
   * Builds a JWT holding a single authority.
   *
   * @param authority the granted authority
   * @return the request post processor
   */
  private static RequestPostProcessor authority(String authority) {
    return jwt().authorities(new SimpleGrantedAuthority(authority));
  }
}
