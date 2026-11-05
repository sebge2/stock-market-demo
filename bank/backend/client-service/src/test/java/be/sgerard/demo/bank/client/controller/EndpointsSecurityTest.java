package be.sgerard.demo.bank.client.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
 * Verifies that the client endpoints are secured by permissions and documented.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class EndpointsSecurityTest {

  /**
   *
   * Valid request body.
   *
   */
  private static final String BODY = "{\"firstName\":\"A\",\"lastName\":\"B\",\"type\":\"INDIVIDUAL\",\"status\":\"ACTIVE\",\"email\":\"a@b.c\"}";

  /**
   *
   * Mock MVC entry point.
   *
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   *
   * Anonymous callers are rejected on every endpoint.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void anonymousIsUnauthorized() throws Exception {
    final String id = UUID.randomUUID().toString();

    mockMvc.perform(get("/clients")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/clients/" + id)).andExpect(status().isUnauthorized());
    mockMvc.perform(post("/clients").contentType("application/json").content(BODY)).andExpect(status().isUnauthorized());
    mockMvc.perform(put("/clients/" + id).contentType("application/json").content(BODY))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(delete("/clients/" + id)).andExpect(status().isUnauthorized());
  }

  /**
   *
   * Callers without the matching permission are forbidden on every endpoint.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void withoutMatchingPermissionIsForbidden() throws Exception {
    final String id = UUID.randomUUID().toString();

    mockMvc.perform(get("/clients").with(authority("SCOPE_client.read"))).andExpect(status().isForbidden());
    mockMvc.perform(get("/clients").with(authority("CLIENT_CREATE"))).andExpect(status().isForbidden());
    mockMvc.perform(get("/clients/" + id).with(authority("EMPLOYEE_LIST"))).andExpect(status().isForbidden());
    mockMvc.perform(post("/clients").with(authority("CLIENT_LIST")).contentType("application/json").content(BODY))
        .andExpect(status().isForbidden());
    mockMvc.perform(put("/clients/" + id).with(authority("CLIENT_LIST")).contentType("application/json").content(BODY))
        .andExpect(status().isForbidden());
    mockMvc.perform(delete("/clients/" + id).with(authority("CLIENT_UPDATE"))).andExpect(status().isForbidden());
  }

  /**
   *
   * Callers with the matching permission are allowed, unknown clients give a 404.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void withMatchingPermissionIsOk() throws Exception {
    mockMvc.perform(get("/clients").with(authority("CLIENT_LIST"))).andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(3));
    mockMvc.perform(get("/clients/00000000-0000-4000-d000-000000000001").with(authority("CLIENT_LIST")))
        .andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Alice"))
        .andExpect(jsonPath("$.email").doesNotExist());
    mockMvc.perform(get("/clients/" + UUID.randomUUID()).with(authority("CLIENT_LIST")))
        .andExpect(status().isNotFound());
    mockMvc.perform(put("/clients/00000000-0000-4000-d000-000000000001").with(authority("CLIENT_UPDATE"))
        .contentType("application/json").content("{\"firstName\":\"Alicia\",\"lastName\":\"Martin\",\"type\":\"INDIVIDUAL\",\"status\":\"ACTIVE\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Alicia"));
    mockMvc.perform(delete("/clients/00000000-0000-4000-d000-000000000003").with(authority("CLIENT_DELETE")))
        .andExpect(status().isNoContent());
  }

  /**
   *
   * Invalid payloads and pagination parameters are rejected.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void invalidInputIsRejected() throws Exception {
    mockMvc.perform(post("/clients").with(authority("CLIENT_CREATE")).contentType("application/json")
        .content("{\"firstName\":\"\",\"lastName\":\"B\",\"type\":\"INDIVIDUAL\",\"status\":\"ACTIVE\",\"email\":\"a@b.c\"}")).andExpect(status().isBadRequest());
    mockMvc.perform(post("/clients").with(authority("CLIENT_CREATE")).contentType("application/json")
        .content("{\"firstName\":\"A\",\"lastName\":\"B\",\"type\":\"INDIVIDUAL\",\"status\":\"ACTIVE\"}")).andExpect(status().isBadRequest());
    mockMvc.perform(get("/clients?size=0").with(authority("CLIENT_LIST"))).andExpect(status().isBadRequest());
  }

  /**
   *
   * The API documentation is public and declares the OAuth2 flow of the user service.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void documentsOAuth2Flow() throws Exception {
    mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
        .andExpect(jsonPath("$.components.securitySchemes.oauth2.flows.authorizationCode.authorizationUrl")
            .value("http://localhost:9000/oauth2/authorize"))
        .andExpect(jsonPath("$.components.securitySchemes.oauth2.flows.authorizationCode.scopes['client.read']").exists())
        .andExpect(jsonPath("$.components.schemas.Client").exists())
        .andExpect(jsonPath("$.components.schemas.ClientRequest").exists())
        .andExpect(jsonPath("$.components.schemas.ClientDto").doesNotExist());
  }

  /**
   *
   * Builds a JWT holding a single authority.
   *
   * @param authority the granted authority
   * @return the request post processor
   *
   */
  private static RequestPostProcessor authority(String authority) {
    return jwt().authorities(new SimpleGrantedAuthority(authority));
  }
}
