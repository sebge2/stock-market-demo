package be.sgerard.demo.exchange.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Configures the OpenAPI documentation with OAuth2 authentication.
 *
 */
@Configuration
public class OpenApiConfiguration {

  /** Name of the OAuth2 security scheme. */
  public static final String OAUTH2_SCHEME = "oauth2";

  /**
   *
   * Describes the API and its OAuth2 authorization-code flow.
   *
   * @param baseUrl public base URL of this service
   * @return OpenAPI definition
   */
  @Bean
  public OpenAPI openApi(@Value("${exchange.user-service.base-url}") String baseUrl) {
    final OAuthFlow flow = new OAuthFlow()
        .authorizationUrl(baseUrl + "/oauth2/authorize")
        .tokenUrl(baseUrl + "/oauth2/token")
        .scopes(new Scopes().addString("user.read", "Read the current user profile"));
    return new OpenAPI()
        .info(new Info().title("Exchange User Service API").version("0.1.0"))
        .components(new Components().addSecuritySchemes(OAUTH2_SCHEME,
            new SecurityScheme().type(SecurityScheme.Type.OAUTH2).flows(new OAuthFlows().authorizationCode(flow))));
  }
}
