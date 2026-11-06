package be.sgerard.demo.bank.client.repository;

import be.sgerard.demo.bank.client.model.Client;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Persistence of clients.
 *
 */
public interface ClientRepository extends JpaRepository<Client, UUID> {
}
