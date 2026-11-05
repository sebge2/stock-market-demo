package be.sgerard.demo.portfolio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 *
 * Container of the positions held by a client. A client owns at most one portfolio.
 *
 */
@Entity
@Table(name = "portfolio", uniqueConstraints = @UniqueConstraint(name = "uk_portfolio_client", columnNames = "client_id"))
public class Portfolio {

    /**
     *
     * Portfolio identifier.
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     *
     * Identifier of the owning client.
     *
     */
    @NotNull
    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    /**
     *
     * Creation date.
     *
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     *
     * Last update date.
     *
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     *
     * Positions of the portfolio.
     *
     */
    @OneToMany(mappedBy = "portfolio")
    private List<Position> positions = new ArrayList<>();

    /**
     *
     * Constructor required by JPA.
     *
     */
    protected Portfolio() {
        // required by JPA
    }

    /**
     *
     * Creates a portfolio.
     *
     * @param clientId the identifier of the owning client
     *
     */
    public Portfolio(UUID clientId) {
        this.clientId = clientId;
    }

    /**
     *
     * Initializes the audit timestamps.
     *
     */
    @PrePersist
    void onCreate() {
        final LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    /**
     *
     * Refreshes the update timestamp.
     *
     */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     *
     * @return the identifier
     *
     */
    public UUID getId() {
        return id;
    }

    /**
     *
     * @return the identifier of the owning client
     *
     */
    public UUID getClientId() {
        return clientId;
    }

    /**
     *
     * @param clientId the identifier of the owning client
     *
     */
    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    /**
     *
     * @return the creation date
     *
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @return the last update date
     *
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @return the read-only positions
     *
     */
    public List<Position> getPositions() {
        return Collections.unmodifiableList(positions);
    }

    @Override
    public String toString() {
        return "Portfolio{id=" + id + ", clientId=" + clientId + ", createdAt=" + createdAt + "}";
    }
}
