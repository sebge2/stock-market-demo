package be.sgerard.demo.user.mapper;

import be.sgerard.demo.user.model.OAuthClient;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Component;

/**
 * Maps OAuth2 client entities to the registered clients of the authorization server, and back.
 */
@Component
public class RegisteredClientMapper {

    /**
     * Converts an entity to a registered client.
     *
     * @param client the entity
     * @return the registered client
     *
     */
    public RegisteredClient toRegisteredClient(OAuthClient client) {
        final RegisteredClient.Builder builder = RegisteredClient.withId(client.getId().toString())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .clientAuthenticationMethod(new ClientAuthenticationMethod(client.getAuthenticationMethod()))
                .authorizationGrantTypes(types -> client.getGrantTypes()
                        .forEach(type -> types.add(new AuthorizationGrantType(type))))
                .redirectUris(uris -> uris.addAll(client.getRedirectUris()))
                .scopes(scopes -> scopes.addAll(client.getScopes()))
                .clientSettings(ClientSettings.builder().requireProofKey(client.isRequireProofKey())
                        .requireAuthorizationConsent(client.isRequireAuthorizationConsent()).build());
        return builder.build();
    }

    /**
     * Copies the data of a registered client to an entity (the identifier is left untouched).
     *
     * @param registeredClient the registered client
     * @param client           the entity to update
     */
    public void updateEntity(RegisteredClient registeredClient, OAuthClient client) {
        client.setClientId(registeredClient.getClientId());
        client.setClientSecret(registeredClient.getClientSecret());
        client.setAuthenticationMethod(registeredClient.getClientAuthenticationMethods().iterator().next().getValue());
        client.getGrantTypes().clear();
        registeredClient.getAuthorizationGrantTypes().forEach(type -> client.getGrantTypes().add(type.getValue()));
        client.getRedirectUris().clear();
        client.getRedirectUris().addAll(registeredClient.getRedirectUris());
        client.getScopes().clear();
        client.getScopes().addAll(registeredClient.getScopes());
        client.setRequireProofKey(registeredClient.getClientSettings().isRequireProofKey());
        client.setRequireAuthorizationConsent(registeredClient.getClientSettings().isRequireAuthorizationConsent());
    }
}
