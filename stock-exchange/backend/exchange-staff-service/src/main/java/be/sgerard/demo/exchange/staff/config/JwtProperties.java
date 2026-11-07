package be.sgerard.demo.exchange.staff.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT configuration properties for OAuth2 resource server.
 *
 * This component holds the configuration for JWT validation including
 * issuer URI and JWKS endpoint.
 */
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
public class JwtProperties {

	/**
	 * The issuer URI where the OAuth2 authorization server is located.
	 */
	private String issuerUri;

	/**
	 * The JWKS (JSON Web Key Set) endpoint URI for retrieving public keys.
	 */
	private String jwkSetUri;

	/**
	 * Gets the issuer URI.
	 *
	 * @return the issuer URI
	 */
	public String getIssuerUri() {
		return issuerUri;
	}

	/**
	 * Sets the issuer URI.
	 *
	 * @param issuerUri the issuer URI to set
	 */
	public void setIssuerUri(final String issuerUri) {
		this.issuerUri = issuerUri;
	}

	/**
	 * Gets the JWKS endpoint URI.
	 *
	 * @return the JWKS endpoint URI
	 */
	public String getJwkSetUri() {
		return jwkSetUri;
	}

	/**
	 * Sets the JWKS endpoint URI.
	 *
	 * @param jwkSetUri the JWKS endpoint URI to set
	 */
	public void setJwkSetUri(final String jwkSetUri) {
		this.jwkSetUri = jwkSetUri;
	}
}
