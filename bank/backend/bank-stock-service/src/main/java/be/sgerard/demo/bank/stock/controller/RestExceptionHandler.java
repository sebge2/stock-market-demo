package be.sgerard.demo.bank.stock.controller;

import be.sgerard.demo.bank.stock.exception.DependencyUnavailableException;
import be.sgerard.demo.bank.stock.exception.PortfolioNotFoundException;
import be.sgerard.demo.bank.stock.exception.QuotationNotFoundException;
import be.sgerard.demo.bank.stock.exception.ValuationFailedException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * Translates business and integration failures to HTTP problem details. Internal details of remote failures
 * are logged but never returned to the caller.
 *
 */
@RestControllerAdvice
public class RestExceptionHandler {

  /**
   *
   * Logger.
   *
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

  /**
   *
   * Answers 404 when the portfolio or the quotation does not exist.
   *
   * @param exception the failure
   * @return the problem detail
   *
   */
  @ExceptionHandler({PortfolioNotFoundException.class, QuotationNotFoundException.class})
  public ProblemDetail handleNotFound(RuntimeException exception) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  /**
   *
   * Answers 422 when the portfolio cannot be valued.
   *
   * @param exception the failure
   * @return the problem detail
   *
   */
  @ExceptionHandler(ValuationFailedException.class)
  public ProblemDetail handleValuationFailed(ValuationFailedException exception) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
  }

  /**
   *
   * Answers 500 when a remote service is unavailable.
   *
   * @param exception the failure
   * @return the problem detail
   *
   */
  @ExceptionHandler(DependencyUnavailableException.class)
  public ProblemDetail handleDependencyUnavailable(DependencyUnavailableException exception) {
    LOGGER.error("Dependency failure", exception);
    return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "A required service is unavailable");
  }

  /**
   *
   * Answers 400 when a request parameter violates its constraints.
   *
   * @return the problem detail
   *
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleConstraintViolation() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request parameter");
  }
}
