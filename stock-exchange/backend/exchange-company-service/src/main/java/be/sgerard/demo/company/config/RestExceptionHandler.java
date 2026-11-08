package be.sgerard.demo.company.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global REST exception handler for handling exceptions across the application.
 *
 * This handler provides consistent error responses for authentication,
 * authorization, and other exceptions.
 */
@RestControllerAdvice
public class RestExceptionHandler {

	/**
	 * Handles authentication exceptions.
	 *
	 * @param ex the authentication exception
	 * @param request the web request
	 * @return the error response
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Object> handleAuthenticationException(
		final AuthenticationException ex,
		final WebRequest request
	) {
		final Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.UNAUTHORIZED.value());
		body.put("error", "Unauthorized");
		body.put("message", "Authentication failed: " + ex.getMessage());
		body.put("path", request.getDescription(false).replace("uri=", ""));

		return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handles access denied exceptions.
	 *
	 * @param ex the access denied exception
	 * @param request the web request
	 * @return the error response
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Object> handleAccessDeniedException(
		final AccessDeniedException ex,
		final WebRequest request
	) {
		final Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.FORBIDDEN.value());
		body.put("error", "Forbidden");
		body.put("message", "Access denied: " + ex.getMessage());
		body.put("path", request.getDescription(false).replace("uri=", ""));

		return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
	}

	/**
	 * Handles exceptions carrying their own HTTP status (not found, conflict...).
	 *
	 * @param ex the exception
	 * @return the error response
	 */
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Object> handleResponseStatus(final ResponseStatusException ex) {
		return error(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason());
	}

	/**
	 * Handles invalid request bodies.
	 *
	 * @param ex the exception
	 * @return the error response
	 */
	@ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
	public ResponseEntity<Object> handleInvalidRequest(final Exception ex) {
		return error(HttpStatus.BAD_REQUEST, "Invalid request");
	}

	/**
	 * Handles violations of the database constraints (duplicates, referenced rows).
	 *
	 * @param ex the exception
	 * @return the error response
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrity(final DataIntegrityViolationException ex) {
		return error(HttpStatus.CONFLICT, "The data conflicts with existing data");
	}

	/**
	 * Builds an error response.
	 *
	 * @param status the HTTP status
	 * @param message the error message
	 * @return the error response
	 */
	private ResponseEntity<Object> error(final HttpStatus status, final String message) {
		final Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		return new ResponseEntity<>(body, status);
	}

	/**
	 * Handles generic exceptions.
	 *
	 * @param ex the exception
	 * @param request the web request
	 * @return the error response
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGlobalException(
		final Exception ex,
		final WebRequest request
	) {
		final Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		body.put("error", "Internal Server Error");
		body.put("message", ex.getMessage());
		body.put("path", request.getDescription(false).replace("uri=", ""));

		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
