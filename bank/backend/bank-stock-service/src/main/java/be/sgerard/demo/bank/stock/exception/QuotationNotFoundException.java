package be.sgerard.demo.bank.stock.exception;

/**
 *
 * Raised when the stock exchange has no quotation for the requested stock.
 *
 */
public class QuotationNotFoundException extends RuntimeException {

  /**
   *
   * Serialization identifier.
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   *
   * Creates the exception.
   *
   * @param message description of the failure
   *
   */
  public QuotationNotFoundException(String message) {
    super(message);
  }

  /**
   *
   * Creates the exception with its cause.
   *
   * @param message description of the failure
   * @param cause underlying failure
   *
   */
  public QuotationNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
