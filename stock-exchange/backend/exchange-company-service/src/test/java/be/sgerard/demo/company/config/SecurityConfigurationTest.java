package be.sgerard.demo.company.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Verifies the OAuth2 resource-server security, the public documentation and the error handling.
 *
 */
@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Import(SecurityConfigurationTest.ProbeConfiguration.class)
class SecurityConfigurationTest {

  /** Mock MVC entry point. */
  @Autowired
  private MockMvc mockMvc;

  /**
   *
   * Anonymous callers must be rejected on protected endpoints.
   *
   * @throws Exception on failure
   */
  @Test
  void anonymousIsUnauthorized() throws Exception {
    mockMvc.perform(get("/probe/admin")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/unknown")).andExpect(status().isUnauthorized());
  }

  /**
   *
   * A malformed bearer token is rejected.
   *
   * @throws Exception on failure
   */
  @Test
  void invalidTokenIsUnauthorized() throws Exception {
    mockMvc.perform(get("/probe/admin").header("Authorization", "Bearer not-a-jwt"))
        .andExpect(status().isUnauthorized());
  }

  /**
   *
   * Authenticated callers lacking the required authority are forbidden with a problem response.
   *
   * @throws Exception on failure
   */
  @Test
  void withoutRequiredAuthorityIsForbidden() throws Exception {
    mockMvc.perform(get("/probe/admin").with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_user.read"))))
        .andExpect(status().isForbidden()).andExpect(jsonPath("$.status").value(403));
  }

  /**
   *
   * Callers holding the required authority are allowed.
   *
   * @throws Exception on failure
   */
  @Test
  void withRequiredAuthorityIsOk() throws Exception {
    mockMvc.perform(get("/probe/admin").with(jwt().authorities(new SimpleGrantedAuthority("COMPANY_LIST"))))
        .andExpect(status().isOk());
  }

  /**
   *
   * Authenticated requests to unknown resources are answered with 404.
   *
   * @throws Exception on failure
   */
  @Test
  void unknownResourceIsNotFound() throws Exception {
    mockMvc.perform(get("/unknown").with(jwt())).andExpect(status().isNotFound());
  }

  /**
   *
   * Unexpected failures yield a 500 problem response that does not leak the cause.
   *
   * @throws Exception on failure
   */
  @Test
  void unexpectedFailureIsInternalServerError() throws Exception {
    mockMvc.perform(get("/probe/failure").with(jwt())).andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.detail").value("Unexpected error"));
  }

  /**
   *
   * The API documentation and the health probe are public, and describe the OAuth2 scheme.
   *
   * @throws Exception on failure
   */
  @Test
  void documentationAndHealthArePublic() throws Exception {
    mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
        .andExpect(jsonPath("$.info.title").value("Exchange Company Service"))
        .andExpect(jsonPath("$.info.version").value("1.0.0"))
        .andExpect(jsonPath("$.components.securitySchemes.oauth2.type").value("oauth2"))
        .andExpect(jsonPath("$.components.securitySchemes.oauth2.flows.authorizationCode.tokenUrl")
            .value("http://localhost:8091/oauth2/token"));
    mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
  }

  /**
   *
   * Registers a probe controller exercising method-level security.
   *
   */
  @TestConfiguration
  static class ProbeConfiguration {

    /**
     *
     * Declares the probe controller.
     *
     * @return the probe controller
     */
    @org.springframework.context.annotation.Bean
    ProbeController probeController() {
      return new ProbeController();
    }
  }

  /**
   *
   * Test-only controller protected with method-level security.
   *
   */
  @RestController
  static class ProbeController {

    /**
     *
     * Endpoint requiring the COMPANY_LIST authority.
     *
     * @return a constant payload
     */
    @GetMapping("/probe/admin")
    @PreAuthorize("hasAuthority('COMPANY_LIST')")
    String admin() {
      return "ok";
    }

    /**
     *
     * Endpoint failing unexpectedly.
     *
     * @return never returns
     */
    @GetMapping("/probe/failure")
    String failure() {
      throw new IllegalStateException("secret internal detail");
    }
  }
}
