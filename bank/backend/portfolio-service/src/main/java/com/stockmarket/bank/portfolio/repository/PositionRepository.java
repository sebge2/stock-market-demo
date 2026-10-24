package com.stockmarket.bank.portfolio.repository;

import com.stockmarket.bank.portfolio.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PositionRepository extends JpaRepository<Position, UUID> {

    List<Position> findByPortfolioId(UUID portfolioId);

    Optional<Position> findByPortfolioIdAndStockId(UUID portfolioId, UUID stockId);
}
