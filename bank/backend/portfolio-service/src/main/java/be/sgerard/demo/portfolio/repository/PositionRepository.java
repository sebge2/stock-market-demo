package be.sgerard.demo.portfolio.repository;

import be.sgerard.demo.portfolio.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * Persistence of positions.
 *
 */
public interface PositionRepository extends JpaRepository<Position, UUID> {

    /**
     *
     * Finds the positions of a portfolio.
     *
     * @param portfolioId the portfolio identifier
     * @return the positions
     *
     */
    List<Position> findByPortfolioId(UUID portfolioId);

    /**
     *
     * Finds the position of a stock within a portfolio.
     *
     * @param portfolioId the portfolio identifier
     * @param stockId the stock identifier
     * @return the position, if any
     *
     */
    Optional<Position> findByPortfolioIdAndStockId(UUID portfolioId, UUID stockId);
}
