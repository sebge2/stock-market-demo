package be.sgerard.demo.portfolio.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 *
 * Public representation of the position held on a stock.
 *
 */
@Schema(name = "Position")
public class PositionDto {

  /**
   *
   * Position identifier.
   *
   */
  private UUID id;

  /**
   *
   * Identifier of the stock.
   *
   */
  private UUID stockId;

  /**
   *
   * Symbol of the stock.
   *
   */
  private String stockSymbol;

  /**
   *
   * Held quantity.
   *
   */
  private BigDecimal quantity;

  /**
   *
   * Average unit cost.
   *
   */
  private BigDecimal averageCost;

  /**
   *
   * Creation date.
   *
   */
  private LocalDateTime createdAt;

  /**
   *
   * Last update date.
   *
   */
  private LocalDateTime lastUpdatedAt;

  /**
   *
   * Purchases making up the position.
   *
   */
  private List<PurchaseDto> purchases;

  /**
   *
   * @return the position identifier
   *
   */
  public UUID getId() {
    return id;
  }

  /**
   *
   * @param id the position identifier
   *
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   *
   * @return the identifier of the stock
   *
   */
  public UUID getStockId() {
    return stockId;
  }

  /**
   *
   * @param stockId the identifier of the stock
   *
   */
  public void setStockId(UUID stockId) {
    this.stockId = stockId;
  }

  /**
   *
   * @return the symbol of the stock
   *
   */
  public String getStockSymbol() {
    return stockSymbol;
  }

  /**
   *
   * @param stockSymbol the symbol of the stock
   *
   */
  public void setStockSymbol(String stockSymbol) {
    this.stockSymbol = stockSymbol;
  }

  /**
   *
   * @return the held quantity
   *
   */
  public BigDecimal getQuantity() {
    return quantity;
  }

  /**
   *
   * @param quantity the held quantity
   *
   */
  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  /**
   *
   * @return the average unit cost
   *
   */
  public BigDecimal getAverageCost() {
    return averageCost;
  }

  /**
   *
   * @param averageCost the average unit cost
   *
   */
  public void setAverageCost(BigDecimal averageCost) {
    this.averageCost = averageCost;
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
   * @param createdAt the creation date
   *
   */
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   *
   * @return the last update date
   *
   */
  public LocalDateTime getLastUpdatedAt() {
    return lastUpdatedAt;
  }

  /**
   *
   * @param lastUpdatedAt the last update date
   *
   */
  public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
    this.lastUpdatedAt = lastUpdatedAt;
  }

  /**
   *
   * @return the purchases making up the position
   *
   */
  public List<PurchaseDto> getPurchases() {
    return purchases;
  }

  /**
   *
   * @param purchases the purchases making up the position
   *
   */
  public void setPurchases(List<PurchaseDto> purchases) {
    this.purchases = purchases;
  }
}
