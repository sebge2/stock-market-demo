package be.sgerard.demo.company.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
