package be.sgerard.demo.portfolio.mapper;

import be.sgerard.demo.portfolio.model.Portfolio;
import be.sgerard.demo.portfolio.model.Position;
import be.sgerard.demo.portfolio.model.Purchase;
import be.sgerard.demo.portfolio.model.PurchaseReceipt;
import be.sgerard.demo.portfolio.model.dto.PortfolioDto;
import be.sgerard.demo.portfolio.model.dto.PortfolioRequestDto;
import be.sgerard.demo.portfolio.model.dto.PositionDto;
import be.sgerard.demo.portfolio.model.dto.PurchaseDto;
import be.sgerard.demo.portfolio.model.dto.PurchaseReceiptDto;
import org.springframework.stereotype.Component;

/**
 *
 * Maps portfolio entities to their API representations and back.
 *
 */
@Component
public class PortfolioMapper {

  /**
   *
   * Creates a portfolio entity from request data.
   *
   * @param request the portfolio request
   * @return the portfolio entity
   *
   */
  public Portfolio toEntity(PortfolioRequestDto request) {
    return new Portfolio(request.getClientId());
  }

  /**
   *
   * Updates a portfolio entity with the request data.
   *
   * @param request the portfolio request
   * @param portfolio the entity to update
   *
   */
  public void updateEntity(PortfolioRequestDto request, Portfolio portfolio) {
    portfolio.setClientId(request.getClientId());
  }

  /**
   *
   * Converts a portfolio, with its positions, to its API representation.
   *
   * @param portfolio the portfolio entity
   * @return the portfolio DTO
   *
   */
  public PortfolioDto toDto(Portfolio portfolio) {
    final PortfolioDto dto = new PortfolioDto();
    dto.setId(portfolio.getId());
    dto.setClientId(portfolio.getClientId());
    dto.setCreatedAt(portfolio.getCreatedAt());
    dto.setUpdatedAt(portfolio.getUpdatedAt());
    dto.setPositions(portfolio.getPositions().stream().map(this::toDto).toList());
    return dto;
  }

  /**
   *
   * Converts a position, with its purchases, to its API representation.
   *
   * @param position the position entity
   * @return the position DTO
   *
   */
  public PositionDto toDto(Position position) {
    final PositionDto dto = new PositionDto();
    dto.setId(position.getId());
    dto.setStockId(position.getStockId());
    dto.setStockSymbol(position.getStockSymbol());
    dto.setQuantity(position.getQuantity());
    dto.setAverageCost(position.getAverageCost());
    dto.setCreatedAt(position.getCreatedAt());
    dto.setLastUpdatedAt(position.getLastUpdatedAt());
    dto.setPurchases(position.getPurchases().stream().map(this::toDto).toList());
    return dto;
  }

  /**
   *
   * Converts a purchase to its API representation.
   *
   * @param purchase the purchase entity
   * @return the purchase DTO
   *
   */
  public PurchaseDto toDto(Purchase purchase) {
    final PurchaseDto dto = new PurchaseDto();
    dto.setId(purchase.getId());
    dto.setQuantity(purchase.getQuantity());
    dto.setUnitPrice(purchase.getUnitPrice());
    dto.setTotalAmount(purchase.getTotalAmount());
    dto.setPurchaseDate(purchase.getPurchaseDate());
    dto.setStatus(purchase.getStatus().name());
    dto.setReceipt(purchase.getReceipt() == null ? null : toDto(purchase.getReceipt()));
    return dto;
  }

  /**
   *
   * Converts a purchase receipt to its API representation.
   *
   * @param receipt the receipt entity
   * @return the receipt DTO
   *
   */
  public PurchaseReceiptDto toDto(PurchaseReceipt receipt) {
    final PurchaseReceiptDto dto = new PurchaseReceiptDto();
    dto.setId(receipt.getId());
    dto.setReceiptNumber(receipt.getReceiptNumber());
    dto.setExchangeOrderId(receipt.getExchangeOrderId());
    dto.setTransactionDate(receipt.getTransactionDate());
    dto.setNotes(receipt.getNotes());
    return dto;
  }
}
