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

import org.springframework.beans.factory.annotation.Value;
import be.sgerard.demo.user.model.Permission;
import java.util.stream.Stream;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
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
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 *
 * Configures the Bank OAuth2 and OpenID Connect authorization server.
 *
 */
@Configuration
@EnableMethodSecurity
public class AuthorizationServerConfiguration {

    /**
     * Client id of the public client used by Swagger UI.
     */
    public static final String SWAGGER_CLIENT_ID = "swagger-ui";

    /** Name of the access-token claim holding the user authorities. */
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
        http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()).formLogin(Customizer.withDefaults())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    /**
     *
     * Registers local OAuth2 clients.
     *
     * @param baseUrl public base URL of this service
     * @return registered client repository
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(
            @Value("${bank.user-service.base-url}") String baseUrl) {
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
        RegisteredClient swagger = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(SWAGGER_CLIENT_ID)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(baseUrl + "/swagger-ui/oauth2-redirect.html")
                .scope("user.read")
                .clientSettings(ClientSettings.builder().requireProofKey(true).requireAuthorizationConsent(false).build())
                .build();
        return new InMemoryRegisteredClientRepository(frontend, swagger);
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
     * @param baseUrl public base URL of this service
     * @return authorization-server settings
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings(
            @Value("${bank.user-service.base-url}") String baseUrl) {
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
