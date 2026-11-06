package be.sgerard.demo.portfolio.repository;

import be.sgerard.demo.portfolio.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 *
 * Persistence of portfolios.
 *
 */
public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {

    /**
     *
     * Finds the portfolio of a client.
     *
     * @param clientId the client identifier
     * @return the portfolio, if any
     *
     */
    Optional<Portfolio> findByClientId(UUID clientId);
}
