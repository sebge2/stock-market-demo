package com.stockmarket.bank.portfolio.repository;

import com.stockmarket.bank.portfolio.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {

    Optional<Portfolio> findByClientId(UUID clientId);
}
