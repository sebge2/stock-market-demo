package be.sgerard.demo.portfolio.model;

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
import java.util.UUID;

/**
 * Aggregate of the purchases of a given stock within a {@link Portfolio}.
 */
@Entity
@Table(name = "position", uniqueConstraints = @UniqueConstraint(
        name = "uk_position_portfolio_stock", columnNames = {"portfolio_id", "stock_id"}))
public class Position {

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
     * Field portfolio.
     *
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false, updatable = false)
    private Portfolio portfolio;

    /**
     *
     * Field stock id.
     *
     */
    @NotNull
    @Column(name = "stock_id", nullable = false, updatable = false)
    private UUID stockId;

    /**
     *
     * Field stock symbol.
     *
     */
    @NotBlank
    @Size(max = 50)
    @Column(name = "stock_symbol", nullable = false, length = 50)
    private String stockSymbol;

    /**
     *
     * Field quantity.
     *
     */
    @NotNull
    @PositiveOrZero
    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    /**
     *
     * Field average cost.
     *
     */
    @NotNull
    @PositiveOrZero
    @Column(name = "average_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal averageCost;

    /**
     *
     * Field created at.
     *
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     *
     * Field last updated at.
     *
     */
    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    /**
     *
     * Field purchases.
     *
     */
    @OneToMany(mappedBy = "position")
    private List<Purchase> purchases = new ArrayList<>();

    /**
     *
     * Constructor required by JPA.
     *
     */
    protected Position() {
        // required by JPA
    }

    /**
     *
     * Creates the entity.
     *
     */
    public Position(Portfolio portfolio, UUID stockId, String stockSymbol, BigDecimal quantity, BigDecimal averageCost) {
        this.portfolio = portfolio;
        this.stockId = stockId;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.averageCost = averageCost;
    }

    /**
     *
     * Lifecycle hook maintaining the audit timestamps.
     *
     */
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        lastUpdatedAt = now;
    }

    /**
     *
     * Lifecycle hook maintaining the audit timestamps.
     *
     */
    @PreUpdate
    void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
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
     * @return the portfolio
     *
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     *
     * @return the stock id
     *
     */
    public UUID getStockId() {
        return stockId;
    }

    /**
     *
     * @return the stock symbol
     *
     */
    public String getStockSymbol() {
        return stockSymbol;
    }

    /**
     *
     * @param stockSymbol the stock symbol
     *
     */
    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
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
     * @param quantity the quantity
     *
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     *
     * @return the average cost
     *
     */
    public BigDecimal getAverageCost() {
        return averageCost;
    }

    /**
     *
     * @param averageCost the average cost
     *
     */
    public void setAverageCost(BigDecimal averageCost) {
        this.averageCost = averageCost;
    }

    /**
     *
     * @return the created at
     *
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @return the last updated at
     *
     */
    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    /**
     *
     * @return the purchases
     *
     */
    public List<Purchase> getPurchases() {
        return Collections.unmodifiableList(purchases);
    }

    @Override
    public String toString() {
        return "Position{id=" + id + ", stockId=" + stockId + ", stockSymbol='" + stockSymbol
                + "', quantity=" + quantity + ", averageCost=" + averageCost + "}";
    }
}
