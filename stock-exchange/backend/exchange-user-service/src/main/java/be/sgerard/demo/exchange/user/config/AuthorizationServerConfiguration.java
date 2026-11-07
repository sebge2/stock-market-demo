package be.sgerard.demo.exchange.user.config;

import be.sgerard.demo.exchange.user.repository.UserAccountRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import be.sgerard.demo.exchange.user.model.Permission;
import be.sgerard.demo.exchange.user.model.RoleStatus;

import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 *
 * Configures the Exchange OAuth2 and OpenID Connect authorization server.
 *
 */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtSigningProperties.class)
public class AuthorizationServerConfiguration {

    /**
     * Name of the access-token claim holding the user authorities.
     */
    public static final String AUTHORITIES_CLAIM = "authorities";

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
        http.cors(Customizer.withDefaults());
        http.exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/login"), new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));
        return http.build();
    }

    /**
     *
     * Configures form login and bearer-token (JWT) authentication for the application endpoints.
     *
     * @param http HTTP security builder
     * @return application filter chain
     * @throws Exception when security cannot be configured
     */
    @Bean
    @Order(2)
    public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()).formLogin(form -> form.loginPage("/login").permitAll())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    /**
     *
     * Allows browser clients (such as the Swagger UI of other services) served from the configured origins to call
     * the token, JWK set and user-info endpoints.
     *
     * @param allowedOrigins origins allowed to call this server from a browser
     * @return the CORS configuration source used by the security filter chains
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${exchange.user-service.cors.allowed-origins:}") List<String> allowedOrigins) {
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
     * Creates the JWK source from the signing keys configured in the service properties.
     *
     * @param properties configured signing keys
     * @return JWK source
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource(JwtSigningProperties properties) {
        final RSAKey rsaKey = new RSAKey.Builder(parsePublicKey(properties.publicKey()))
                .privateKey(parsePrivateKey(properties.privateKey())).keyID(properties.keyId()).build();
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
     * @param baseUrl public base URL of this service
     * @return authorization-server settings
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings(
            @Value("${exchange.user-service.base-url}") String baseUrl) {
        return AuthorizationServerSettings.builder().issuer(baseUrl).build();
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
                .authorities(account.getRoles().stream()
                        .filter(role -> role.getStatus() == RoleStatus.ACTIVE)
                        .flatMap(role -> Stream.concat(
                                Stream.of("ROLE_" + role.getName()),
                                role.getPermissions().stream().map(Permission::getCode)))
                        .distinct().map(SimpleGrantedAuthority::new).toList())
                .build()).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    /**
     *
     * Copies the authorities of the authenticated user (roles and permissions) into access tokens.
     *
     * @return token customizer adding the {@value #AUTHORITIES_CLAIM} claim
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> authoritiesTokenCustomizer() {
        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                context.getClaims().claim(AUTHORITIES_CLAIM, context.getPrincipal().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList());
            }
        };
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
     * Parses a PEM encoded PKCS#8 RSA private key.
     *
     * @param pem PEM content
     * @return the private key
     */
    private static RSAPrivateKey parsePrivateKey(String pem) {
        try {
            return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(decodePem(pem)));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Invalid JWT signing private key (expected PKCS#8 PEM)", exception);
        }
    }

    /**
     *
     * Parses a PEM encoded X.509 RSA public key.
     *
     * @param pem PEM content
     * @return the public key
     */
    private static RSAPublicKey parsePublicKey(String pem) {
        try {
            return (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(decodePem(pem)));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Invalid JWT signing public key (expected X.509 PEM)", exception);
        }
    }

    /**
     *
     * Extracts the binary content of a PEM block.
     *
     * @param pem PEM content
     * @return the decoded bytes
     */
    private static byte[] decodePem(String pem) {
        return Base64.getMimeDecoder().decode(pem.replaceAll("-----(BEGIN|END) [A-Z ]+-----", "").trim());
    }
}
