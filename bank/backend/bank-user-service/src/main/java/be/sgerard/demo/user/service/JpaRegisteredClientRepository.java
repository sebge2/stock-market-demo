package be.sgerard.demo.user.service;

import be.sgerard.demo.user.mapper.RegisteredClientMapper;
import be.sgerard.demo.user.model.OAuthClient;
import be.sgerard.demo.user.repository.OAuthClientRepository;

import java.util.UUID;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registered client repository of the authorization server, backed by the database.
 */
@Service
@Transactional(readOnly = true)
public class JpaRegisteredClientRepository implements RegisteredClientRepository {

    /**
     * OAuth2 client persistence.
     */
    private final OAuthClientRepository oAuthClientRepository;

    /**
     * Mapper between entities and registered clients.
     */
    private final RegisteredClientMapper mapper;

    /**
     * Creates the repository.
     *
     * @param oAuthClientRepository OAuth2 client persistence
     * @param mapper                mapper between entities and registered clients
     */
    public JpaRegisteredClientRepository(OAuthClientRepository oAuthClientRepository,
                                         RegisteredClientMapper mapper) {
        this.oAuthClientRepository = oAuthClientRepository;
        this.mapper = mapper;
    }

    /**
     * Creates or updates a client.
     *
     * @param registeredClient the client to save
     */
    @Override
    @Transactional
    public void save(RegisteredClient registeredClient) {
        final OAuthClient client = findEntity(registeredClient.getId()).orElseGet(
                () -> new OAuthClient(registeredClient.getClientId(), "none"));
        mapper.updateEntity(registeredClient, client);
        oAuthClientRepository.save(client);
    }

    /**
     *
     * @param id client identifier
     * @return the client, or {@code null} when unknown
     *
     */
    @Override
    public RegisteredClient findById(String id) {
        return findEntity(id).map(mapper::toRegisteredClient).orElse(null);
    }

    /**
     *
     * @param clientId public identifier of the client
     * @return the client, or {@code null} when unknown
     *
     */
    @Override
    public RegisteredClient findByClientId(String clientId) {
        return oAuthClientRepository.findByClientId(clientId).map(mapper::toRegisteredClient).orElse(null);
    }

    /**
     *
     * Finds an entity from the string form of its identifier.
     *
     */
    private java.util.Optional<OAuthClient> findEntity(String id) {
        try {
            return oAuthClientRepository.findById(UUID.fromString(id));
        } catch (IllegalArgumentException exception) {
            return java.util.Optional.empty();
        }
    }
}
