package com.stockmarket.bank.portfolio.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate of the purchases of a given stock within a {@link Portfolio}.
 */
@Entity
@Table(name = "position", uniqueConstraints = @UniqueConstraint(
        name = "uk_position_portfolio_stock", columnNames = {"portfolio_id", "stock_id"}))
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false, updatable = false)
    private Portfolio portfolio;

    @NotNull
    @Column(name = "stock_id", nullable = false, updatable = false)
    private UUID stockId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "stock_symbol", nullable = false, length = 50)
    private String stockSymbol;

    @NotNull
    @PositiveOrZero
    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @NotNull
    @PositiveOrZero
    @Column(name = "average_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal averageCost;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    @OneToMany(mappedBy = "position")
    private final List<Purchase> purchases = new ArrayList<>();

    protected Position() {
        // required by JPA
    }

    public Position(Portfolio portfolio, UUID stockId, String stockSymbol, BigDecimal quantity, BigDecimal averageCost) {
        this.portfolio = portfolio;
        this.stockId = stockId;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.averageCost = averageCost;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        lastUpdatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public UUID getStockId() {
        return stockId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(BigDecimal averageCost) {
        this.averageCost = averageCost;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public List<Purchase> getPurchases() {
        return Collections.unmodifiableList(purchases);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position other)) {
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
        return "Position{id=" + id + ", stockId=" + stockId + ", stockSymbol='" + stockSymbol
                + "', quantity=" + quantity + ", averageCost=" + averageCost + "}";
    }
}
