package be.sgerard.demo.portfolio.repository;

import be.sgerard.demo.portfolio.model.PurchaseReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 *
 * Persistence of purchase receipts.
 *
 */
public interface PurchaseReceiptRepository extends JpaRepository<PurchaseReceipt, UUID> {

    /**
     *
     * Finds a receipt by its business number.
     *
     * @param receiptNumber the receipt number
     * @return the receipt, if any
     *
     */
    Optional<PurchaseReceipt> findByReceiptNumber(String receiptNumber);
}
