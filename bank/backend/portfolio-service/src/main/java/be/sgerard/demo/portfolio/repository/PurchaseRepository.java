package be.sgerard.demo.portfolio.repository;

import be.sgerard.demo.portfolio.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {

    List<Purchase> findByPositionId(UUID positionId);
}
