package be.sgerard.demo.portfolio.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * Translates validation failures of request parameters to client errors.
 *
 */
@RestControllerAdvice
public class RestExceptionHandler {

  /**
   *
   * Answers 400 when a request parameter violates its constraints (for instance an invalid page size).
   *
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleConstraintViolation() {
    // the status code is the whole answer
  }
}
