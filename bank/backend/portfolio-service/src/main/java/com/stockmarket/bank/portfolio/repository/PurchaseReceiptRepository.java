package com.stockmarket.bank.portfolio.repository;

import com.stockmarket.bank.portfolio.domain.PurchaseReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PurchaseReceiptRepository extends JpaRepository<PurchaseReceipt, UUID> {

    Optional<PurchaseReceipt> findByReceiptNumber(String receiptNumber);
}
