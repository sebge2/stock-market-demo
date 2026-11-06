package be.sgerard.demo.bank.stock.client;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 *
 * Gives access to the bearer token of the caller of the current request, so that remote calls are performed
 * (and audited) on behalf of that caller.
 *
 */
final class CallerToken {

  /**
   *
   * Utility class.
   *
   */
  private CallerToken() {
  }

  /**
   *
   * Extracts the bearer token of the authenticated caller of the current request.
   *
   * @return the raw JWT value
   * @throws IllegalStateException if the current caller is not authenticated with a JWT
   *
   */
  static String current() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
      return jwtAuthentication.getToken().getTokenValue();
    }
    throw new IllegalStateException("Cannot call a remote service: no JWT in the security context");
  }
}
