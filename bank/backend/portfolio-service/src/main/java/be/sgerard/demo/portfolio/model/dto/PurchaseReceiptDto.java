package be.sgerard.demo.portfolio.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * Public representation of the receipt of a purchase.
 *
 */
@Schema(name = "PurchaseReceipt")
public class PurchaseReceiptDto {

  /**
   *
   * Receipt identifier.
   *
   */
  private UUID id;

  /**
   *
   * Business receipt number.
   *
   */
  private String receiptNumber;

  /**
   *
   * Identifier of the bank transaction.
   *
   */
  private UUID bankTransactionId;

  /**
   *
   * Identifier of the exchange order.
   *
   */
  private UUID exchangeOrderId;

  /**
   *
   * Transaction date.
   *
   */
  private LocalDateTime transactionDate;

  /**
   *
   * Free notes.
   *
   */
  private String notes;

  /**
   *
   * @return the receipt identifier
   *
   */
  public UUID getId() {
    return id;
  }

  /**
   *
   * @param id the receipt identifier
   *
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   *
   * @return the business receipt number
   *
   */
  public String getReceiptNumber() {
    return receiptNumber;
  }

  /**
   *
   * @param receiptNumber the business receipt number
   *
   */
  public void setReceiptNumber(String receiptNumber) {
    this.receiptNumber = receiptNumber;
  }

  /**
   *
   * @return the identifier of the bank transaction
   *
   */
  public UUID getBankTransactionId() {
    return bankTransactionId;
  }

  /**
   *
   * @param bankTransactionId the identifier of the bank transaction
   *
   */
  public void setBankTransactionId(UUID bankTransactionId) {
    this.bankTransactionId = bankTransactionId;
  }

  /**
   *
   * @return the identifier of the exchange order
   *
   */
  public UUID getExchangeOrderId() {
    return exchangeOrderId;
  }

  /**
   *
   * @param exchangeOrderId the identifier of the exchange order
   *
   */
  public void setExchangeOrderId(UUID exchangeOrderId) {
    this.exchangeOrderId = exchangeOrderId;
  }

  /**
   *
   * @return the transaction date
   *
   */
  public LocalDateTime getTransactionDate() {
    return transactionDate;
  }

  /**
   *
   * @param transactionDate the transaction date
   *
   */
  public void setTransactionDate(LocalDateTime transactionDate) {
    this.transactionDate = transactionDate;
  }

  /**
   *
   * @return the free notes
   *
   */
  public String getNotes() {
    return notes;
  }

  /**
   *
   * @param notes the free notes
   *
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }
}
