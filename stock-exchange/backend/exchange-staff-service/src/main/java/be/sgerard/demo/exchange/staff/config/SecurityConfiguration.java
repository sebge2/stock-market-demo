package be.sgerard.demo.exchange.staff.config;

import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * Secures the service as an OAuth2 resource server validating the JWT tokens issued by the exchange user service.
 *
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

  /** Name of the access-token claim holding the user authorities. */
  public static final String AUTHORITIES_CLAIM = "authorities";

  /**
   *
   * Requires a valid bearer token on every request, except for the health probe and the API documentation.
   *
   * @param http HTTP security builder
   * @return the application filter chain
   * @throws Exception when security cannot be configured
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            .anyRequest().authenticated())
        .oauth2ResourceServer(resourceServer -> resourceServer
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
    return http.build();
  }

  /**
   *
   * Maps both the token scopes and the authorities claim to granted authorities.
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
}
