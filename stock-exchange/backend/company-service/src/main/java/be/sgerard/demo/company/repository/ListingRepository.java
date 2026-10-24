package be.sgerard.demo.company.repository;

import be.sgerard.demo.company.model.Listing;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link Listing} entities.
 *
 */
public interface ListingRepository extends JpaRepository<Listing, UUID> {

  /**
   *
   * Finds a listing by exchange and ticker.
   *
   * @param exchangeId the exchange identifier
   * @param ticker the ticker symbol
   * @return the matching listing, if any
   */
  Optional<Listing> findByExchangeIdAndTicker(UUID exchangeId, String ticker);

  /**
   *
   * Finds the listings of a security.
   *
   * @param securityId the security identifier
   * @return the listings
   */
  List<Listing> findBySecurityId(UUID securityId);
}
