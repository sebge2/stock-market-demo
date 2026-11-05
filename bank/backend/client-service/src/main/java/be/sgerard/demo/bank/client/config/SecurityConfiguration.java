package be.sgerard.demo.bank.client.config;

import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 *
 * Secures the REST endpoints as an OAuth2 resource server: the bearer tokens are issued by the bank user
 * service and the permissions of the user are read from their {@value #AUTHORITIES_CLAIM} claim.
 *
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

  /**
   *
   * Name of the access-token claim holding the user authorities.
   *
   */
  public static final String AUTHORITIES_CLAIM = "authorities";

  /**
   *
   * Configures the stateless bearer-token security of the application.
   *
   * @param http HTTP security builder
   * @return the security filter chain
   * @throws Exception when security cannot be configured
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            .anyRequest().authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .oauth2ResourceServer(resourceServer -> resourceServer
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
    return http.build();
  }

  /**
   *
   * Allows browser clients served from the configured origins (such as the bank frontend) to call the API.
   *
   * @param allowedOrigins origins allowed to call this service from a browser
   * @return the CORS configuration source used by the security filter chain
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource(
      @Value("${bank.client-service.cors.allowed-origins:}") List<String> allowedOrigins) {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(allowedOrigins.stream().filter(origin -> !origin.isBlank()).toList());
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  /**
   *
   * Converts a JWT into authorities made of its scopes and of the {@value #AUTHORITIES_CLAIM} claim.
   *
   * @return JWT authentication converter
   */
  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    final JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
    final JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
    authorities.setAuthoritiesClaimName(AUTHORITIES_CLAIM);
    authorities.setAuthorityPrefix("");
    final JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwt -> Stream
        .concat(scopes.convert(jwt).stream(), authorities.convert(jwt).stream()).toList());
    return converter;
  }
}
