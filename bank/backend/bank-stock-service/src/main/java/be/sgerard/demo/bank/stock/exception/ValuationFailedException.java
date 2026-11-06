package be.sgerard.demo.bank.stock.exception;

/**
 *
 * Raised when a portfolio cannot be valued, for instance because a quotation is missing or stale.
 *
 */
public class ValuationFailedException extends RuntimeException {

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
  public ValuationFailedException(String message) {
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
  public ValuationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
