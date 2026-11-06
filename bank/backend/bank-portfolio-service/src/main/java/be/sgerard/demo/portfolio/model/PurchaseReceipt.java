package be.sgerard.demo.portfolio.model;

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
import java.util.UUID;

/**
 * Receipt proving a purchase, referencing the exchange order.
 */
@Entity
@Table(name = "purchase_receipt", uniqueConstraints = @UniqueConstraint(
        name = "uk_purchase_receipt_number", columnNames = "receipt_number"))
public class PurchaseReceipt {

    /**
     *
     * Field id.
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     *
     * Business number, e.g. {@code RCP-20250101-0001}.
     *
     */
    @NotBlank
    @Size(max = 50)
    @Column(name = "receipt_number", nullable = false, length = 50)
    private String receiptNumber;

    /**
     *
     * Field exchange order id.
     *
     */
    @NotNull
    @Column(name = "exchange_order_id", nullable = false)
    private UUID exchangeOrderId;

    /**
     *
     * Field transaction date.
     *
     */
    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    /**
     *
     * Field notes.
     *
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     *
     * Field created at.
     *
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     *
     * Constructor required by JPA.
     *
     */
    protected PurchaseReceipt() {
        // required by JPA
    }

    /**
     *
     * Creates the entity.
     *
     */
    public PurchaseReceipt(String receiptNumber, UUID exchangeOrderId,
                           LocalDateTime transactionDate, String notes) {
        this.receiptNumber = receiptNumber;
        this.exchangeOrderId = exchangeOrderId;
        this.transactionDate = transactionDate;
        this.notes = notes;
    }

    /**
     *
     * Lifecycle hook maintaining the audit timestamps.
     *
     */
    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     *
     * @return the id
     *
     */
    public UUID getId() {
        return id;
    }

    /**
     *
     * @return the receipt number
     *
     */
    public String getReceiptNumber() {
        return receiptNumber;
    }

    /**
     *
     * @return the exchange order id
     *
     */
    public UUID getExchangeOrderId() {
        return exchangeOrderId;
    }

    /**
     *
     * @return the transaction date
     *
     */
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    /**
     *
     * @return the notes
     *
     */
    public String getNotes() {
        return notes;
    }

    /**
     *
     * @param notes the notes
     *
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     *
     * @return the created at
     *
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "PurchaseReceipt{id=" + id + ", receiptNumber='" + receiptNumber
                + "', exchangeOrderId=" + exchangeOrderId + "}";
    }
}
