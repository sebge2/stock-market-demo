package be.sgerard.demo.portfolio.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * Public representation of a purchase of a stock.
 *
 */
@Schema(name = "Purchase")
public class PurchaseDto {

  /**
   *
   * Purchase identifier.
   *
   */
  private UUID id;

  /**
   *
   * Purchased quantity.
   *
   */
  private BigDecimal quantity;

  /**
   *
   * Unit price.
   *
   */
  private BigDecimal unitPrice;

  /**
   *
   * Total amount.
   *
   */
  private BigDecimal totalAmount;

  /**
   *
   * Purchase date.
   *
   */
  private LocalDateTime purchaseDate;

  /**
   *
   * Status of the purchase (PENDING, EXECUTED or CANCELLED).
   *
   */
  private String status;

  /**
   *
   * Receipt of the purchase, absent while the purchase is pending.
   *
   */
  private PurchaseReceiptDto receipt;

  /**
   *
   * @return the purchase identifier
   *
   */
  public UUID getId() {
    return id;
  }

  /**
   *
   * @param id the purchase identifier
   *
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   *
   * @return the purchased quantity
   *
   */
  public BigDecimal getQuantity() {
    return quantity;
  }

  /**
   *
   * @param quantity the purchased quantity
   *
   */
  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  /**
   *
   * @return the unit price
   *
   */
  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  /**
   *
   * @param unitPrice the unit price
   *
   */
  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  /**
   *
   * @return the total amount
   *
   */
  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  /**
   *
   * @param totalAmount the total amount
   *
   */
  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  /**
   *
   * @return the purchase date
   *
   */
  public LocalDateTime getPurchaseDate() {
    return purchaseDate;
  }

  /**
   *
   * @param purchaseDate the purchase date
   *
   */
  public void setPurchaseDate(LocalDateTime purchaseDate) {
    this.purchaseDate = purchaseDate;
  }

  /**
   *
   * @return the status of the purchase (PENDING, EXECUTED or CANCELLED)
   *
   */
  public String getStatus() {
    return status;
  }

  /**
   *
   * @param status the status of the purchase (PENDING, EXECUTED or CANCELLED)
   *
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   *
   * @return the receipt of the purchase, absent while the purchase is pending
   *
   */
  public PurchaseReceiptDto getReceipt() {
    return receipt;
  }

  /**
   *
   * @param receipt the receipt of the purchase, absent while the purchase is pending
   *
   */
  public void setReceipt(PurchaseReceiptDto receipt) {
    this.receipt = receipt;
  }
}
