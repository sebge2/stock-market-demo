package be.sgerard.demo.company.repository;

import be.sgerard.demo.company.model.Exchange;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link Exchange} entities.
 *
 */
public interface ExchangeRepository extends JpaRepository<Exchange, UUID> {

  /**
   *
   * Finds an exchange by Market Identifier Code.
   *
   * @param mic the MIC
   * @return the matching exchange, if any
   */
  Optional<Exchange> findByMic(String mic);
}
