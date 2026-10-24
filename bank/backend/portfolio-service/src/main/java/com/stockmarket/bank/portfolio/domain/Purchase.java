package com.stockmarket.bank.portfolio.domain;

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
import java.util.Objects;
import java.util.UUID;

/**
 * Trace of an individual purchase of a stock.
 * <p>
 * The receipt is optional because a {@link PurchaseStatus#PENDING} purchase has none yet.
 */
@Entity
@Table(name = "purchase", uniqueConstraints = @UniqueConstraint(name = "uk_purchase_receipt", columnNames = "receipt_id"))
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false, updatable = false)
    private Position position;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    private PurchaseReceipt receipt;

    @NotNull
    @Positive
    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @NotNull
    @Positive
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @NotNull
    @Positive
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @NotNull
    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PurchaseStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public PurchaseReceipt getReceipt() {
        return receipt;
    }

    public void setReceipt(PurchaseReceipt receipt) {
        this.receipt = receipt;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Purchase other)) {
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
        return "Purchase{id=" + id + ", quantity=" + quantity + ", unitPrice=" + unitPrice
                + ", totalAmount=" + totalAmount + ", status=" + status + "}";
    }
}
