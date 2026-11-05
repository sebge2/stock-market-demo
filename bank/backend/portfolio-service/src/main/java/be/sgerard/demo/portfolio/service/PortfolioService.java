package be.sgerard.demo.portfolio.service;

import be.sgerard.demo.portfolio.mapper.PortfolioMapper;
import be.sgerard.demo.portfolio.model.Portfolio;
import be.sgerard.demo.portfolio.model.dto.PortfolioDto;
import be.sgerard.demo.portfolio.model.dto.PortfolioRequestDto;
import be.sgerard.demo.portfolio.repository.PortfolioRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Business logic of the portfolios.
 *
 */
@Service
public class PortfolioService {

  /**
   *
   * Portfolio repository.
   *
   */
  private final PortfolioRepository portfolioRepository;

  /**
   *
   * Portfolio mapper.
   *
   */
  private final PortfolioMapper portfolioMapper;

  /**
   *
   * Creates the service.
   *
   * @param portfolioRepository the portfolio repository
   * @param portfolioMapper the portfolio mapper
   *
   */
  public PortfolioService(PortfolioRepository portfolioRepository, PortfolioMapper portfolioMapper) {
    this.portfolioRepository = portfolioRepository;
    this.portfolioMapper = portfolioMapper;
  }

  /**
   *
   * Returns a page of portfolios.
   *
   * @param pageable the page request
   * @return the portfolio page
   *
   */
  @Transactional(readOnly = true)
  public Page<PortfolioDto> findAll(Pageable pageable) {
    return portfolioRepository.findAll(pageable).map(portfolioMapper::toDto);
  }

  /**
   *
   * Returns a portfolio.
   *
   * @param id the portfolio identifier
   * @return the portfolio DTO
   * @throws ResponseStatusException if the portfolio is not found
   *
   */
  @Transactional(readOnly = true)
  public PortfolioDto findById(UUID id) {
    return portfolioMapper.toDto(findEntity(id));
  }

  /**
   *
   * Creates a portfolio.
   *
   * @param request the creation request
   * @return the created portfolio DTO
   * @throws ResponseStatusException if the client already owns a portfolio
   *
   */
  @Transactional
  public PortfolioDto create(PortfolioRequestDto request) {
    ensureClientHasNoOtherPortfolio(request.getClientId(), null);
    return portfolioMapper.toDto(portfolioRepository.saveAndFlush(portfolioMapper.toEntity(request)));
  }

  /**
   *
   * Updates a portfolio.
   *
   * @param id the portfolio identifier
   * @param request the update request
   * @return the updated portfolio DTO
   * @throws ResponseStatusException if the portfolio is not found or the client owns another portfolio
   *
   */
  @Transactional
  public PortfolioDto update(UUID id, PortfolioRequestDto request) {
    final Portfolio portfolio = findEntity(id);
    ensureClientHasNoOtherPortfolio(request.getClientId(), id);
    portfolioMapper.updateEntity(request, portfolio);
    return portfolioMapper.toDto(portfolioRepository.saveAndFlush(portfolio));
  }

  /**
   *
   * Deletes an empty portfolio.
   *
   * @param id the portfolio identifier
   * @throws ResponseStatusException if the portfolio is not found or still holds positions
   *
   */
  @Transactional
  public void delete(UUID id) {
    final Portfolio portfolio = findEntity(id);
    if (!portfolio.getPositions().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "The portfolio still holds positions");
    }
    portfolioRepository.delete(portfolio);
  }

  /**
   *
   * Finds a portfolio entity.
   *
   * @param id the portfolio identifier
   * @return the entity
   * @throws ResponseStatusException if the portfolio is not found
   *
   */
  private Portfolio findEntity(UUID id) {
    return portfolioRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Portfolio not found"));
  }

  /**
   *
   * Checks that a client does not own another portfolio.
   *
   * @param clientId the client identifier
   * @param portfolioId the identifier of the portfolio being updated, null on creation
   * @throws ResponseStatusException if the client already owns a different portfolio
   *
   */
  private void ensureClientHasNoOtherPortfolio(UUID clientId, UUID portfolioId) {
    portfolioRepository.findByClientId(clientId)
        .filter(existing -> !existing.getId().equals(portfolioId))
        .ifPresent(existing -> {
          throw new ResponseStatusException(HttpStatus.CONFLICT, "The client already owns a portfolio");
        });
  }
}
