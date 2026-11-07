package be.sgerard.demo.exchange.staff.config;

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
 * Configures the OpenAPI documentation with the OAuth2 authorization-code flow of the exchange user service.
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
   * @param userServiceBaseUrl public base URL of the exchange user service
   * @return the OpenAPI definition
   */
  @Bean
  public OpenAPI openApi(@Value("${exchange.user-service.base-url}") String userServiceBaseUrl) {
    final OAuthFlow flow = new OAuthFlow()
        .authorizationUrl(userServiceBaseUrl + "/oauth2/authorize")
        .tokenUrl(userServiceBaseUrl + "/oauth2/token")
        .scopes(new Scopes().addString("staff.read", "Access the staff API"));
    return new OpenAPI()
        .info(new Info().title("Exchange Staff Service").version("1.0.0"))
        .components(new Components().addSecuritySchemes(OAUTH2_SCHEME,
            new SecurityScheme().type(SecurityScheme.Type.OAUTH2).flows(new OAuthFlows().authorizationCode(flow))));
  }
}
