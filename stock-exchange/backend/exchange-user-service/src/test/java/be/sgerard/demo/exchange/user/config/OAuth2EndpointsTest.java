package be.sgerard.demo.exchange.user.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 *
 * Verifies the OAuth2 authorization server endpoints (token, JWKS and discovery).
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class OAuth2EndpointsTest {

  /** MVC test client. */
  @Autowired
  private MockMvc mockMvc;

  /**
   *
   * Verifies the JWKS endpoint publishes the signing key without its private part.
   *
   */
  @Test
  void publishesJwks() throws Exception {
    mockMvc.perform(get("/oauth2/jwks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.keys[0].kid").value("exchange-local-dev-key"))
        .andExpect(jsonPath("$.keys[0].kty").value("RSA"))
        .andExpect(jsonPath("$.keys[0].d").doesNotExist());
  }

  /**
   *
   * Verifies the discovery document exposes the issuer and the token endpoint.
   *
   */
  @Test
  void publishesDiscoveryMetadata() throws Exception {
    mockMvc.perform(get("/.well-known/openid-configuration"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.issuer").value("http://localhost:8091"))
        .andExpect(jsonPath("$.token_endpoint").value("http://localhost:8091/oauth2/token"))
        .andExpect(jsonPath("$.jwks_uri").value("http://localhost:8091/oauth2/jwks"));
  }

  /**
   *
   * Verifies the token endpoint rejects an invalid authorization code of a registered public client.
   *
   */
  @Test
  void rejectsInvalidAuthorizationCode() throws Exception {
    mockMvc.perform(post("/oauth2/token").param("grant_type", "authorization_code")
            .param("client_id", "exchange-swagger-ui").param("code", "invalid")
            .param("redirect_uri", "http://localhost:8091/swagger-ui/oauth2-redirect.html")
            .param("code_verifier", "verifier"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("invalid_grant"));
  }

  /**
   *
   * Verifies the token endpoint rejects an unknown client.
   *
   */
  @Test
  void rejectsUnknownClient() throws Exception {
    mockMvc.perform(post("/oauth2/token").param("grant_type", "authorization_code")
            .param("client_id", "unknown").param("code", "invalid").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  /**
   *
   * Verifies the password grant is not available, so credentials cannot be exchanged directly for a token.
   *
   */
  @Test
  void rejectsPasswordGrant() throws Exception {
    mockMvc.perform(post("/oauth2/token").param("grant_type", "password")
            .param("client_id", "exchange-swagger-ui").param("username", "admin").param("password", "changeit").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }
}
