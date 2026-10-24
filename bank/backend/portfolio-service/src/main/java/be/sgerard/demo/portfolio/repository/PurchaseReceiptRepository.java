package be.sgerard.demo.portfolio.repository;

import be.sgerard.demo.portfolio.domain.PurchaseReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseReceiptRepository extends JpaRepository<PurchaseReceipt, UUID> {

    Optional<PurchaseReceipt> findByReceiptNumber(String receiptNumber);
}
