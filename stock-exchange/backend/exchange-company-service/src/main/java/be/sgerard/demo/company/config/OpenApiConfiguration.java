package be.sgerard.demo.company.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for API documentation.
 *
 * This configuration defines the OpenAPI/Swagger documentation for the
 * exchange-company-service, including OAuth2 security scheme.
 */
@Configuration
public class OpenApiConfiguration {

	/**
	 * Creates the OpenAPI documentation bean.
	 *
	 * @param exchangeUserServiceBaseUrl the base URL of the exchange-user-service
	 * @return the OpenAPI bean
	 */
	@Bean
	public OpenAPI openAPI(@Value("${exchange.user-service.base-url:http://localhost:8091}") final String exchangeUserServiceBaseUrl) {
		return new OpenAPI()
			.info(new Info()
				.title("Exchange Company Service")
				.description("Company and listing management microservice for Stock Exchange")
				.version("1.0.0")
			)
			.components(new Components()
				.addSecuritySchemes("OAuth2", new SecurityScheme()
					.type(SecurityScheme.Type.OAUTH2)
					.description("OAuth2 authorization using exchange-user-service")
					.flows(new OAuthFlows()
						.authorizationCode(new OAuthFlow()
							.authorizationUrl(exchangeUserServiceBaseUrl + "/oauth2/authorize")
							.tokenUrl(exchangeUserServiceBaseUrl + "/oauth2/token")
							.scopes(new io.swagger.v3.oas.models.security.Scopes()
								.addString("read:companies", "Read company data")
								.addString("write:companies", "Write company data")
								.addString("admin:companies", "Administrative access to companies")
							)
						)
					)
				)
			);
	}
}
