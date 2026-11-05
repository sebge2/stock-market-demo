package be.sgerard.demo.user.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 *
 * RSA key pair used to sign the JWT access and ID tokens. The same pair must be configured on every
 * instance of the service so that tokens stay valid across instances and restarts.
 *
 * @param keyId identifier published in the JWK set (the {@code kid} header of the tokens)
 * @param privateKey PKCS#8 private key, PEM encoded
 * @param publicKey X.509 public key, PEM encoded
 *
 */
@Validated
@ConfigurationProperties(prefix = "bank.user-service.jwt")
public record JwtSigningProperties(@NotBlank String keyId, @NotBlank String privateKey,
    @NotBlank String publicKey) {
}
