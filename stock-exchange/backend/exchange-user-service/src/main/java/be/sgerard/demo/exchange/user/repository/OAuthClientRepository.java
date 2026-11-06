package be.sgerard.demo.exchange.user.repository;

import be.sgerard.demo.exchange.user.model.OAuthClient;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository for OAuth2 clients.
 *
 */
public interface OAuthClientRepository extends JpaRepository<OAuthClient, UUID> {

    /**
     *
     * Finds a client by its public identifier.
     *
     * @param clientId public identifier of the client
     * @return matching client, if present
     *
     */
    Optional<OAuthClient> findByClientId(String clientId);
}
