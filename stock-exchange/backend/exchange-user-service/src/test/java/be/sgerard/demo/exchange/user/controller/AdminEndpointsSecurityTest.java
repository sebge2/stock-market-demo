package be.sgerard.demo.exchange.user.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 *
 * Verifies that the role and permission endpoints require the matching permissions.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AdminEndpointsSecurityTest {

  /** Mock MVC entry point. */
  @Autowired
  private MockMvc mockMvc;

  /**
   *
   * Anonymous callers must be rejected.
   *
   * @throws Exception on failure
   */
  @Test
  void anonymousIsUnauthorized() throws Exception {
    mockMvc.perform(get("/roles")).andExpect(status().isUnauthorized());
  }

  /**
   *
   * Authenticated callers without the matching permissions are forbidden.
   *
   * @throws Exception on failure
   */
  @Test
  void withoutAdminPermissionIsForbidden() throws Exception {
    mockMvc.perform(get("/roles").with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_user.read"))))
        .andExpect(status().isForbidden());
    mockMvc.perform(get("/permissions").with(jwt())).andExpect(status().isForbidden());
    mockMvc.perform(get("/roles").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CREATE"))))
        .andExpect(status().isForbidden());
    mockMvc.perform(post("/roles").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIST")))
        .with(csrf()).contentType("application/json").content("{\"name\":\"X\",\"status\":\"ACTIVE\"}")).andExpect(status().isForbidden());
    mockMvc.perform(get("/permissions").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIST"))))
        .andExpect(status().isForbidden());
    mockMvc.perform(delete("/permissions/" + UUID.randomUUID())
        .with(jwt().authorities(new SimpleGrantedAuthority("PERMISSION_LIST")))).andExpect(status().isForbidden());
    mockMvc.perform(delete("/roles/" + UUID.randomUUID())
        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIST")))).andExpect(status().isForbidden());
  }

  /**
   *
   * Callers holding the matching permissions are allowed.
   *
   * @throws Exception on failure
   */
  @Test
  void withAdminPermissionIsOk() throws Exception {
    mockMvc.perform(get("/roles").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIST"))))
        .andExpect(status().isOk());
    mockMvc.perform(get("/permissions").with(jwt().authorities(new SimpleGrantedAuthority("PERMISSION_LIST"))))
        .andExpect(status().isOk());
  }
}
