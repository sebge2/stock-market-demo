package be.sgerard.demo.bank.stock.exception;

/**
 *
 * Raised when a remote service stays unavailable or answers unexpectedly after all retries.
 *
 */
public class DependencyUnavailableException extends RuntimeException {

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
  public DependencyUnavailableException(String message) {
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
  public DependencyUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
