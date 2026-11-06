package be.sgerard.demo.bank.stock.client;

import be.sgerard.demo.bank.stock.exception.DependencyUnavailableException;
import be.sgerard.demo.bank.stock.exception.PortfolioNotFoundException;
import be.sgerard.demo.bank.stock.model.dto.RemotePortfolioDto;
import java.util.UUID;

/**
 *
 * Client of the portfolio service.
 *
 */
public interface PortfolioServiceClient {

  /**
   *
   * Retrieves a portfolio with its positions on behalf of the current caller (the bearer token of the
   * current request is forwarded). Transient failures are retried.
   *
   * @param portfolioId identifier of the portfolio
   * @return the portfolio
   * @throws PortfolioNotFoundException if the portfolio does not exist
   * @throws DependencyUnavailableException if the portfolio service fails or is unreachable
   *
   */
  RemotePortfolioDto findPortfolio(UUID portfolioId);
}
