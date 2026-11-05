package be.sgerard.demo.user.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 * An OAuth2 client allowed to request tokens from the authorization server.
 *
 */
@Entity
@Table(name = "oauth_client")
public class OAuthClient {

    /**
     *
     * Internal unique identifier.
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     *
     * Creation date, set automatically on first persist.
     *
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     *
     * Last update date, refreshed automatically on every update.
     *
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     *
     * Public identifier of the client.
     *
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "client_id", nullable = false, unique = true, length = 100)
    private String clientId;

    /**
     *
     * Hashed client secret, {@code null} for public clients.
     *
     */
    @Column(name = "client_secret")
    private String clientSecret;

    /**
     *
     * Client authentication method (for instance {@code none} or {@code client_secret_basic}).
     *
     */
    @NotBlank
    @Size(max = 50)
    @Column(name = "authentication_method", nullable = false, length = 50)
    private String authenticationMethod;

    /**
     *
     * Authorization grant types (for instance {@code authorization_code}).
     *
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "oauth_client_grant_type", joinColumns = @JoinColumn(name = "oauth_client_id"))
    @Column(name = "grant_type", nullable = false, length = 50)
    private Set<String> grantTypes = new LinkedHashSet<>();

    /**
     *
     * Allowed redirect URIs.
     *
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "oauth_client_redirect_uri", joinColumns = @JoinColumn(name = "oauth_client_id"))
    @Column(name = "redirect_uri", nullable = false, length = 500)
    private Set<String> redirectUris = new LinkedHashSet<>();

    /**
     *
     * Allowed scopes.
     *
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "oauth_client_scope", joinColumns = @JoinColumn(name = "oauth_client_id"))
    @Column(name = "scope", nullable = false, length = 100)
    private Set<String> scopes = new LinkedHashSet<>();

    /**
     *
     * Whether the authorization code flow requires PKCE.
     *
     */
    @Column(name = "require_proof_key", nullable = false)
    private boolean requireProofKey;

    /**
     *
     * Whether the user must explicitly consent to the requested scopes.
     *
     */
    @Column(name = "require_authorization_consent", nullable = false)
    private boolean requireAuthorizationConsent;

    /**
     *
     * Constructor required by JPA.
     *
     */
    protected OAuthClient() {
    }

    /**
     *
     * Creates a new instance.
     *
     * @param clientId             public identifier of the client
     * @param authenticationMethod client authentication method
     *
     */
    public OAuthClient(String clientId, String authenticationMethod) {
        this.clientId = clientId;
        this.authenticationMethod = authenticationMethod;
    }

    /**
     *
     * Initializes the audit timestamps.
     *
     */
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     *
     * Refreshes the update timestamp.
     *
     */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     *
     * @return the identifier
     *
     */
    public UUID getId() {
        return id;
    }

    /**
     *
     * @return the public identifier of the client
     *
     */
    public String getClientId() {
        return clientId;
    }

    /**
     *
     * @param clientId the new public identifier
     *
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     *
     * @return the hashed secret, or {@code null} for a public client
     *
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     *
     * @param clientSecret the new hashed secret
     *
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     *
     * @return the client authentication method
     *
     */
    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    /**
     *
     * @param authenticationMethod the new client authentication method
     *
     */
    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    /**
     *
     * @return the grant types
     *
     */
    public Set<String> getGrantTypes() {
        return grantTypes;
    }

    /**
     *
     * @return the redirect URIs
     *
     */
    public Set<String> getRedirectUris() {
        return redirectUris;
    }

    /**
     *
     * @return the scopes
     *
     */
    public Set<String> getScopes() {
        return scopes;
    }

    /**
     *
     * @return whether PKCE is required
     *
     */
    public boolean isRequireProofKey() {
        return requireProofKey;
    }

    /**
     *
     * @param requireProofKey whether PKCE is required
     *
     */
    public void setRequireProofKey(boolean requireProofKey) {
        this.requireProofKey = requireProofKey;
    }

    /**
     *
     * @return whether the user consent is required
     *
     */
    public boolean isRequireAuthorizationConsent() {
        return requireAuthorizationConsent;
    }

    /**
     *
     * @param requireAuthorizationConsent whether the user consent is required
     *
     */
    public void setRequireAuthorizationConsent(boolean requireAuthorizationConsent) {
        this.requireAuthorizationConsent = requireAuthorizationConsent;
    }

    /**
     *
     * Textual representation without the secret.
     *
     */
    @Override
    public String toString() {
        return "OAuthClient{id=" + id + ", clientId=" + clientId + "}";
    }
}
