package be.sgerard.demo.company.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Security configuration for OAuth2 resource server.
 *
 * This configuration enables OAuth2 JWT-based authentication using tokens
 * issued by the exchange-user-service. It also configures method-level security
 * and CORS.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

	/**
	 * Name of the access-token claim holding the user authorities.
	 */
	public static final String AUTHORITIES_CLAIM = "authorities";

	/**
	 * Configures the security filter chain for OAuth2 resource server.
	 *
	 * @param http the HttpSecurity to configure
	 * @return the SecurityFilterChain
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
		@Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/swagger-ui/**",
					"/v3/api-docs/**",
					"/swagger-ui.html",
					"/webjars/**",
					"/actuator/health"
				).permitAll()
				.anyRequest().authenticated()
			)
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

		return http.build();
	}

	/**
	 * Converts a JWT into authorities made of its scopes and of the {@value #AUTHORITIES_CLAIM} claim.
	 *
	 * @return the JWT authentication converter
	 */
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		final JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
		final JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
		authorities.setAuthoritiesClaimName(AUTHORITIES_CLAIM);
		authorities.setAuthorityPrefix("");
		final JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(
			jwt -> Stream.concat(scopes.convert(jwt).stream(), authorities.convert(jwt).stream()).toList());
		return converter;
	}

	/**
	 * Creates a JWT decoder for validating JWT tokens.
	 *
	 * @param jwtProperties the JWT properties containing issuer URI and JWKS endpoint
	 * @return the JwtDecoder
	 */
	@Bean
	public JwtDecoder jwtDecoder(JwtProperties jwtProperties) {
		return NimbusJwtDecoder.withJwkSetUri(jwtProperties.getJwkSetUri())
			.build();
	}

	/**
	 * Configures CORS settings.
	 *
	 * @param allowedOrigins origins allowed by the configured environment, comma-separated when needed
	 * @return the CORS configuration source
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource(
		@Value("${exchange.company-service.cors.allowed-origins:}") String allowedOrigins) {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
			.map(String::trim).filter(origin -> !origin.isBlank()).toList());
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
