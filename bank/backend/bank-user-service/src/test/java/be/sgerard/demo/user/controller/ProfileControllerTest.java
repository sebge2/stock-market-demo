package be.sgerard.demo.user.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 *
 * Verifies the profile endpoint and its protection.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ProfileControllerTest {

  /** MVC test client. */
  @Autowired
  private MockMvc mockMvc;

  /**
   *
   * Verifies the connected user is returned with its username, email and permission names (not identifiers).
   *
   */
  @Test
  void returnsCurrentUser() throws Exception {
    mockMvc.perform(get("/profile").with(jwt().jwt(token -> token.subject("sebastien.gerard"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("sebastien.gerard"))
        .andExpect(jsonPath("$.email").value("sebastien.gerard@local.test"))
        .andExpect(jsonPath("$.permissions", hasItems("ADMIN", "ROLE_LIST", "EMPLOYEE_LIST")))
        .andExpect(jsonPath("$.permissions[?(@ =~ /[0-9a-f]{8}-.*/)]").isEmpty())
        .andExpect(jsonPath("$.length()").value(3));
  }

  /**
   *
   * Verifies an unknown authenticated user is reported as not found.
   *
   */
  @Test
  void returnsNotFoundForUnknownUser() throws Exception {
    mockMvc.perform(get("/profile").with(jwt().jwt(token -> token.subject("ghost"))))
        .andExpect(status().isNotFound());
  }

  /**
   *
   * Verifies anonymous calls are rejected.
   *
   */
  @Test
  void rejectsAnonymousCalls() throws Exception {
    mockMvc.perform(get("/profile")).andExpect(status().isUnauthorized());
  }

  /**
   *
   * Verifies an invalid bearer token is rejected.
   *
   */
  @Test
  void rejectsInvalidToken() throws Exception {
    mockMvc.perform(get("/profile").header("Authorization", "Bearer invalid"))
        .andExpect(status().isUnauthorized());
  }

  /**
   *
   * Verifies the OpenAPI documentation is publicly available.
   *
   */
  @Test
  void exposesOpenApiDocumentation() throws Exception {
    mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
        .andExpect(jsonPath("$.paths['/profile']").exists())
        .andExpect(jsonPath("$.components.securitySchemes.oauth2.type").value("oauth2"));
  }
}
