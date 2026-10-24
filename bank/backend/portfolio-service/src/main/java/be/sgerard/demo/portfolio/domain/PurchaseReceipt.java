package be.sgerard.demo.portfolio.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Receipt proving a purchase, referencing the bank transaction and the exchange order.
 */
@Entity
@Table(name = "purchase_receipt", uniqueConstraints = @UniqueConstraint(
        name = "uk_purchase_receipt_number", columnNames = "receipt_number"))
public class PurchaseReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** Business number, e.g. {@code RCP-20250101-0001}. */
    @NotBlank
    @Size(max = 50)
    @Column(name = "receipt_number", nullable = false, length = 50)
    private String receiptNumber;

    @NotNull
    @Column(name = "bank_transaction_id", nullable = false)
    private UUID bankTransactionId;

    @NotNull
    @Column(name = "exchange_order_id", nullable = false)
    private UUID exchangeOrderId;

    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected PurchaseReceipt() {
        // required by JPA
    }

    public PurchaseReceipt(String receiptNumber, UUID bankTransactionId, UUID exchangeOrderId,
                           LocalDateTime transactionDate, String notes) {
        this.receiptNumber = receiptNumber;
        this.bankTransactionId = bankTransactionId;
        this.exchangeOrderId = exchangeOrderId;
        this.transactionDate = transactionDate;
        this.notes = notes;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public UUID getBankTransactionId() {
        return bankTransactionId;
    }

    public UUID getExchangeOrderId() {
        return exchangeOrderId;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseReceipt other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "PurchaseReceipt{id=" + id + ", receiptNumber='" + receiptNumber
                + "', bankTransactionId=" + bankTransactionId + ", exchangeOrderId=" + exchangeOrderId + "}";
    }
}
