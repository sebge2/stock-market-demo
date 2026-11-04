package be.sgerard.demo.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
   * @return OpenAPI definition
   */
  @Bean
  public OpenAPI openApi() {
    final OAuthFlow flow = new OAuthFlow()
        .authorizationUrl("http://localhost:9000/oauth2/authorize")
        .tokenUrl("http://localhost:9000/oauth2/token")
        .scopes(new Scopes().addString("openid", "OpenID Connect").addString("profile", "User profile"));
    return new OpenAPI()
        .info(new Info().title("Bank User Service API").version("0.1.0"))
        .components(new Components().addSecuritySchemes(OAUTH2_SCHEME,
            new SecurityScheme().type(SecurityScheme.Type.OAUTH2).flows(new OAuthFlows().authorizationCode(flow))));
  }
}
