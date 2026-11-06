package be.sgerard.demo.exchange.user.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 *
 * Verifies the custom login page of the authorization server.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class LoginPageTest {

  /**
   *
   * Mock MVC entry point.
   *
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   *
   * The login page is public and posts the credentials with a CSRF token.
   *
   * @throws Exception on failure
   */
  @Test
  void servesPublicLoginForm() throws Exception {
    mockMvc.perform(get("/login")).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("text/html"))
        .andExpect(content().string(containsString("<h1>Connexion</h1>")))
        .andExpect(content().string(containsString("name=\"username\"")))
        .andExpect(content().string(containsString("name=\"password\"")))
        .andExpect(content().string(containsString("name=\"_csrf\"")))
        .andExpect(content().string(not(containsString("{{"))))
        .andExpect(content().string(not(containsString("role=\"alert\""))));
  }

  /**
   *
   * The page shows a notice after a failed login or a logout.
   *
   * @throws Exception on failure
   */
  @Test
  void showsNotices() throws Exception {
    mockMvc.perform(get("/login").param("error", "")).andExpect(status().isOk())
        .andExpect(content().string(containsString("Identifiant ou mot de passe incorrect.")));
    mockMvc.perform(get("/login").param("logout", "")).andExpect(status().isOk())
        .andExpect(content().string(containsString("Vous avez été déconnecté.")));
  }

  /**
   *
   * A wrong password sends the user back to the login page with the error flag.
   *
   * @throws Exception on failure
   */
  @Test
  void redirectsToLoginOnBadCredentials() throws Exception {
    mockMvc.perform(post("/login").with(csrf()).param("username", "admin").param("password", "wrong"))
        .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login?error"));
  }
}
