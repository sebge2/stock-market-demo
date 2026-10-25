package be.sgerard.demo.user.config;

import be.sgerard.demo.user.repository.UserAccountRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * Configures the Bank OAuth2 and OpenID Connect authorization server.
 *
 */
@Configuration
public class AuthorizationServerConfiguration {

  /**
   *
   * Configures OAuth2 and OIDC protocol endpoints.
   *
   * @param http HTTP security builder
   * @return authorization-server filter chain
   * @throws Exception when security cannot be configured
   */
  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
    http.getConfigurer(
        org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer.class)
        .oidc(Customizer.withDefaults());
    return http.build();
  }

  /**
   *
   * Configures form login for user authentication.
   *
   * @param http HTTP security builder
   * @return application filter chain
   * @throws Exception when security cannot be configured
   */
  @Bean
  @Order(2)
  public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/actuator/**").permitAll()
        .anyRequest().authenticated()).formLogin(Customizer.withDefaults());
    return http.build();
  }

  /**
   *
   * Registers local OAuth2 clients.
   *
   * @return registered client repository
   */
  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient frontend = RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("bank-frontend")
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .redirectUri("http://localhost:4200/login/oauth2/code/bank")
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.PROFILE)
        .scope("user.read")
        .clientSettings(ClientSettings.builder().requireProofKey(true).build())
        .build();
    return new InMemoryRegisteredClientRepository(frontend);
  }

  /**
   *
   * Creates local signing keys for JWT access and ID tokens.
   *
   * @return JWK source
   */
  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    KeyPair keyPair = generateRsaKey();
    RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
        .privateKey((RSAPrivateKey) keyPair.getPrivate()).keyID(UUID.randomUUID().toString()).build();
    return new ImmutableJWKSet<>(new JWKSet(rsaKey));
  }

  /**
   *
   * Exposes a decoder for OIDC endpoints.
   *
   * @param jwkSource signing key source
   * @return JWT decoder
   */
  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

  /**
   *
   * Defines the issuer published by discovery metadata.
   *
   * @return authorization-server settings
   */
  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().issuer("http://localhost:9000").build();
  }

  /**
   *
   * Loads login users from the user-service database.
   *
   * @param userAccountRepository user account repository
   * @return Spring Security user-details service
   */
  @Bean
  public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
    return username -> userAccountRepository.findByUsername(username).map(account -> User
        .withUsername(account.getUsername()).password(account.getPasswordHash())
        .authorities(account.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).toList())
        .build()).orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(username));
  }

  /**
   *
   * Hashes user passwords stored by the authorization server.
   *
   * @return BCrypt password encoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   *
   * Generates an RSA key pair for local JWT signing.
   *
   * @return RSA key pair
   */
  private static KeyPair generateRsaKey() {
    try {
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
      generator.initialize(2048);
      return generator.generateKeyPair();
    } catch (Exception exception) {
      throw new IllegalStateException("Unable to generate the authorization-server signing key", exception);
    }
  }
}
