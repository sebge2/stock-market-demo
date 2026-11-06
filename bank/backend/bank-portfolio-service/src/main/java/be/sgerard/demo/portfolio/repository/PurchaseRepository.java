package be.sgerard.demo.portfolio.repository;

import be.sgerard.demo.portfolio.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 *
 * Persistence of purchases.
 *
 */
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {

    /**
     *
     * Finds the purchases of a position.
     *
     * @param positionId the position identifier
     * @return the purchases
     *
     */
    List<Purchase> findByPositionId(UUID positionId);
}
