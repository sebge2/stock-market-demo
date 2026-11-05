package be.sgerard.demo.portfolio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Trace of an individual purchase of a stock.
 * <p>
 * The receipt is optional because a {@link PurchaseStatus#PENDING} purchase has none yet.
 */
@Entity
@Table(name = "purchase", uniqueConstraints = @UniqueConstraint(name = "uk_purchase_receipt", columnNames = "receipt_id"))
public class Purchase {

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
     * Field position.
     *
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false, updatable = false)
    private Position position;

    /**
     *
     * Field receipt.
     *
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    private PurchaseReceipt receipt;

    /**
     *
     * Field quantity.
     *
     */
    @NotNull
    @Positive
    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    /**
     *
     * Field unit price.
     *
     */
    @NotNull
    @Positive
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    /**
     *
     * Field total amount.
     *
     */
    @NotNull
    @Positive
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    /**
     *
     * Field purchase date.
     *
     */
    @NotNull
    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    /**
     *
     * Field status.
     *
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PurchaseStatus status;

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
    protected Purchase() {
        // required by JPA
    }

    /**
     * Creates a purchase; the total amount is computed as {@code quantity x unitPrice}.
     */
    public Purchase(Position position, BigDecimal quantity, BigDecimal unitPrice,
                    LocalDateTime purchaseDate, PurchaseStatus status) {
        this.position = position;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = quantity == null || unitPrice == null ? null : quantity.multiply(unitPrice);
        this.purchaseDate = purchaseDate;
        this.status = status;
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
     * @return the position
     *
     */
    public Position getPosition() {
        return position;
    }

    /**
     *
     * @return the receipt
     *
     */
    public PurchaseReceipt getReceipt() {
        return receipt;
    }

    /**
     *
     * @param receipt the receipt
     *
     */
    public void setReceipt(PurchaseReceipt receipt) {
        this.receipt = receipt;
    }

    /**
     *
     * @return the quantity
     *
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     *
     * @return the unit price
     *
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     *
     * @return the total amount
     *
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     *
     * @return the purchase date
     *
     */
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    /**
     *
     * @return the status
     *
     */
    public PurchaseStatus getStatus() {
        return status;
    }

    /**
     *
     * @param status the status
     *
     */
    public void setStatus(PurchaseStatus status) {
        this.status = status;
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
        return "Purchase{id=" + id + ", quantity=" + quantity + ", unitPrice=" + unitPrice
                + ", totalAmount=" + totalAmount + ", status=" + status + "}";
    }
}
