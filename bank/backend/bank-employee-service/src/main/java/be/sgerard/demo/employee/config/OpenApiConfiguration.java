package be.sgerard.demo.employee.config;

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
 * Describes the API and its OAuth2 security scheme, backed by the bank user service.
 *
 */
@Configuration
public class OpenApiConfiguration {

  /**
   *
   * Name of the OAuth2 security scheme.
   *
   */
  public static final String OAUTH2_SCHEME = "oauth2";

  /**
   *
   * Declares the authorization code flow of the bank user service.
   *
   * @param userServiceUrl public base URL of the bank user service
   * @return the OpenAPI definition
   */
  @Bean
  public OpenAPI openApi(@Value("${bank.user-service.base-url}") String userServiceUrl) {
    final OAuthFlow flow = new OAuthFlow()
        .authorizationUrl(userServiceUrl + "/oauth2/authorize")
        .tokenUrl(userServiceUrl + "/oauth2/token")
        .scopes(new Scopes().addString("employee.read", "Access the employee API"));
    return new OpenAPI()
        .info(new Info().title("Bank Employee Service API").version("0.1.0"))
        .components(new Components().addSecuritySchemes(OAUTH2_SCHEME,
            new SecurityScheme().type(SecurityScheme.Type.OAUTH2).flows(new OAuthFlows().authorizationCode(flow))));
  }
}
