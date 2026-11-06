package be.sgerard.demo.exchange.user.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 *
 * Verifies Swagger UI can authenticate through OAuth2.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class SwaggerOAuth2ClientTest {

  /** MVC test client. */
  @Autowired
  private MockMvc mockMvc;
  /** Registered OAuth2 clients. */
  @Autowired
  private RegisteredClientRepository clients;

  /**
   *
   * Verifies the Swagger client is a public PKCE client with the Swagger redirect URI.
   *
   */
  @Test
  void registersPublicPkceSwaggerClient() {
    final RegisteredClient client = clients.findByClientId("exchange-swagger-ui");

    assertThat(client).isNotNull();
    assertThat(client.getRedirectUris()).containsExactly("http://localhost:8091/swagger-ui/oauth2-redirect.html");
    assertThat(client.getScopes()).containsExactly("user.read");
    assertThat(client.getClientSettings().isRequireProofKey()).isTrue();
  }

  /**
   *
   * Verifies the company service Swagger client is a public PKCE client with its own redirect URI.
   *
   */
  @Test
  void registersPublicPkceCompanySwaggerClient() {
    final RegisteredClient client = clients.findByClientId("company-swagger-ui");

    assertThat(client).isNotNull();
    assertThat(client.getRedirectUris()).containsExactly("http://localhost:8087/api/company/swagger-ui/oauth2-redirect.html");
    assertThat(client.getScopes()).containsExactly("company.read");
    assertThat(client.getAuthorizationGrantTypes()).extracting(type -> type.getValue())
        .containsExactly("authorization_code");
    assertThat(client.getClientSettings().isRequireProofKey()).isTrue();
  }

  /**
   *
   * Verifies the staff service Swagger client is a public PKCE client with its own redirect URI.
   *
   */
  @Test
  void registersPublicPkceStaffSwaggerClient() {
    final RegisteredClient client = clients.findByClientId("staff-swagger-ui");

    assertThat(client).isNotNull();
    assertThat(client.getRedirectUris()).containsExactly("http://localhost:8086/api/staff/swagger-ui/oauth2-redirect.html");
    assertThat(client.getScopes()).containsExactly("staff.read");
    assertThat(client.getAuthorizationGrantTypes()).extracting(type -> type.getValue())
        .containsExactly("authorization_code");
    assertThat(client.getClientSettings().isRequireProofKey()).isTrue();
  }

  /**
   *
   * Verifies the OpenAPI document declares the OAuth2 flow with the registered scope.
   *
   */
  @Test
  void documentsOAuth2FlowInOpenApi() throws Exception {
    mockMvc.perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.components.securitySchemes.oauth2.flows.authorizationCode.authorizationUrl")
            .value("http://localhost:8091/oauth2/authorize"))
        .andExpect(jsonPath("$.components.securitySchemes.oauth2.flows.authorizationCode.scopes['user.read']").exists());
  }

  /**
   *
   * Verifies the token endpoint answers the CORS preflight of the company Swagger UI origin.
   *
   */
  @Test
  void allowsCorsPreflightOnTokenEndpoint() throws Exception {
    mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options("/oauth2/token")
            .header("Origin", "http://localhost:8087").header("Access-Control-Request-Method", "POST")
            .header("Access-Control-Request-Headers", "content-type"))
        .andExpect(status().isOk())
        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
            .string("Access-Control-Allow-Origin", "http://localhost:8087"));
  }
}
