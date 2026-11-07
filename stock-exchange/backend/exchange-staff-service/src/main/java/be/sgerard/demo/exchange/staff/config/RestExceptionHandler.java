package be.sgerard.demo.exchange.staff.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * Translates exceptions into RFC 7807 problem responses without leaking internal details.
 *
 */
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  /** Logger of this handler. */
  private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

  /**
   *
   * Maps a method-security denial to a 403 response.
   *
   * @param exception the denial
   * @return the problem detail
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ProblemDetail handleAccessDenied(AccessDeniedException exception) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access is denied");
  }

  /**
   *
   * Maps any unexpected failure to a generic 500 response and logs the cause.
   *
   * @param exception the failure
   * @return the problem detail
   */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpected(Exception exception) {
    LOGGER.error("Unexpected error", exception);
    return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
  }
}
