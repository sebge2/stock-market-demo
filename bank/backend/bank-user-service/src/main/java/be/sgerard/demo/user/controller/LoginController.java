package be.sgerard.demo.user.controller;

import io.swagger.v3.oas.annotations.Hidden;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

/**
 *
 * Serves the login page of the authorization server. The page is public (it is the entry point of the
 * authentication) and is not part of the REST API, hence hidden from the OpenAPI documentation.
 *
 */
@RestController
@Hidden
public class LoginController {

  /**
   *
   * HTML template of the page.
   *
   */
  private final String template;

  /**
   *
   * Creates the controller and loads the page template.
   *
   */
  public LoginController() {
    try {
      this.template = new String(new ClassPathResource("login/login.html").getInputStream().readAllBytes(),
          StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   *
   * Renders the login form, with a notice after a failed login or a logout.
   *
   * @param error present after a failed login
   * @param logout present after a logout
   * @param csrfToken CSRF token to embed in the form
   * @return the login page
   *
   */
  @GetMapping(value = "/login", produces = "text/html;charset=UTF-8")
  public ResponseEntity<String> login(@RequestParam(required = false) String error,
      @RequestParam(required = false) String logout, CsrfToken csrfToken) {
    final String notice;
    if (error != null) {
      notice = "<div class=\"notice notice-error\" role=\"alert\">Identifiant ou mot de passe incorrect.</div>";
    } else if (logout != null) {
      notice = "<div class=\"notice notice-info\" role=\"status\">Vous avez été déconnecté.</div>";
    } else {
      notice = "";
    }
    final String html = template.replace("{{notice}}", notice)
        .replace("{{csrfName}}", HtmlUtils.htmlEscape(csrfToken.getParameterName()))
        .replace("{{csrfToken}}", HtmlUtils.htmlEscape(csrfToken.getToken()));
    return ResponseEntity.ok().contentType(new MediaType("text", "html", StandardCharsets.UTF_8)).header("Cache-Control", "no-store").body(html);
  }
}
