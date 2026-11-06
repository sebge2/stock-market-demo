package be.sgerard.demo.exchange.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.sgerard.demo.exchange.user.repository.UserAccountRepository;
import be.sgerard.demo.exchange.user.service.AvatarProcessorTestSupport;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 *
 * Verifies the user endpoints: permissions required and basic CRUD behaviour.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class UserControllerTest {

  /**
   *
   * Mock MVC entry point.
   *
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   *
   * User account persistence.
   *
   */
  @Autowired
  private UserAccountRepository userAccountRepository;

  /**
   *
   * Password encoder.
   *
   */
  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   *
   * Every user endpoint requires its own permission.
   *
   * @throws Exception on failure
   */
  @Test
  void requiresMatchingPermissions() throws Exception {
    final String body = "{\"username\":\"u1\",\"fullName\":\"Full u1\",\"email\":\"u1@local.test\",\"password\":\"password1\"}";
    final UUID id = UUID.randomUUID();

    mockMvc.perform(get("/users")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/users").with(authority("USER_CREATE"))).andExpect(status().isForbidden());
    mockMvc.perform(get("/users/" + id).with(authority("ROLE_LIST"))).andExpect(status().isForbidden());
    mockMvc.perform(post("/users").with(authority("USER_LIST")).with(csrf()).contentType("application/json")
        .content(body)).andExpect(status().isForbidden());
    mockMvc.perform(put("/users/" + id).with(authority("USER_LIST")).with(csrf()).contentType("application/json")
        .content(body)).andExpect(status().isForbidden());
    mockMvc.perform(delete("/users/" + id).with(authority("USER_LIST"))).andExpect(status().isForbidden());
    mockMvc.perform(get("/users").with(authority("USER_LIST"))).andExpect(status().isOk());
  }

  /**
   *
   * A user can be created, updated and deleted; the password is hashed and never returned.
   *
   * @throws Exception on failure
   */
  @Test
  void managesUserLifecycle() throws Exception {
    final String created = mockMvc.perform(post("/users").with(authority("USER_CREATE")).with(csrf())
            .contentType("application/json")
            .content("{\"username\":\"jane\",\"fullName\":\"Full jane\",\"email\":\"jane@local.test\",\"password\":\"password1\"}"))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.username").value("jane"))
        .andExpect(jsonPath("$.password").doesNotExist()).andExpect(jsonPath("$.passwordHash").doesNotExist())
        .andReturn().getResponse().getContentAsString();
    final UUID id = UUID.fromString(created.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1"));
    assertThat(passwordEncoder.matches("password1", userAccountRepository.findById(id).orElseThrow()
        .getPasswordHash())).isTrue();

    mockMvc.perform(put("/users/" + id).with(authority("USER_UPDATE")).with(csrf()).contentType("application/json")
        .content("{\"username\":\"jane\",\"fullName\":\"Full jane\",\"email\":\"jane.doe@local.test\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("jane.doe@local.test"));
    assertThat(passwordEncoder.matches("password1", userAccountRepository.findById(id).orElseThrow()
        .getPasswordHash())).isTrue();

    mockMvc.perform(delete("/users/" + id).with(authority("USER_DELETE")).with(csrf()))
        .andExpect(status().isNoContent());
    mockMvc.perform(get("/users/" + id).with(authority("USER_LIST"))).andExpect(status().isNotFound());
  }

  /**
   *
   * The avatar is reduced to 128x128 pixels at most, kept when absent on update, and removed when empty.
   *
   * @throws Exception on failure
   */
  @Test
  void storesReducedAvatar() throws Exception {
    final String image = AvatarProcessorTestSupport.image(800, 400);
    final String created = mockMvc.perform(post("/users").with(authority("USER_CREATE")).with(csrf())
            .contentType("application/json")
            .content("{\"username\":\"avatar1\",\"fullName\":\"Full avatar\",\"email\":\"avatar1@local.test\","
                + "\"password\":\"password1\",\"avatar\":\"" + image + "\"}"))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.avatar").isNotEmpty())
        .andReturn().getResponse().getContentAsString();
    final UUID id = UUID.fromString(created.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1"));
    final String stored = userAccountRepository.findById(id).orElseThrow().getAvatar();
    assertThat(AvatarProcessorTestSupport.width(stored)).isEqualTo(128);
    assertThat(AvatarProcessorTestSupport.height(stored)).isEqualTo(64);

    mockMvc.perform(put("/users/" + id).with(authority("USER_UPDATE")).with(csrf()).contentType("application/json")
        .content("{\"username\":\"avatar1\",\"fullName\":\"Full avatar\",\"email\":\"avatar1@local.test\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.avatar").value(stored));

    mockMvc.perform(put("/users/" + id).with(authority("USER_UPDATE")).with(csrf()).contentType("application/json")
        .content("{\"username\":\"avatar1\",\"fullName\":\"Full avatar\",\"email\":\"avatar1@local.test\",\"avatar\":\"\"}"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.avatar").doesNotExist());

    mockMvc.perform(put("/users/" + id).with(authority("USER_UPDATE")).with(csrf()).contentType("application/json")
        .content("{\"username\":\"avatar1\",\"fullName\":\"Full avatar\",\"email\":\"avatar1@local.test\",\"avatar\":\"bm90IGFuIGltYWdl\"}"))
        .andExpect(status().isBadRequest());
  }

  /**
   *
   * The exchange frontend may read and update users from the browser (CORS preflight).
   *
   * @throws Exception on failure
   */
  @Test
  void allowsFrontendUserUpdatesThroughCors() throws Exception {
    final String url = "/users/" + UUID.randomUUID();

    mockMvc.perform(options(url).header("Origin", "http://localhost:3001")
            .header("Access-Control-Request-Method", "PUT").header("Access-Control-Request-Headers", "authorization,content-type"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3001"));
  }

  /**
   *
   * The password is mandatory on creation and usernames are unique.
   *
   * @throws Exception on failure
   */
  @Test
  void rejectsMissingPasswordAndDuplicates() throws Exception {
    mockMvc.perform(post("/users").with(authority("USER_CREATE")).with(csrf()).contentType("application/json")
        .content("{\"username\":\"nopass\",\"fullName\":\"Full nopass\",\"email\":\"nopass@local.test\"}")).andExpect(status().isBadRequest());
    mockMvc.perform(post("/users").with(authority("USER_CREATE")).with(csrf()).contentType("application/json")
        .content("{\"username\":\"admin\",\"fullName\":\"Full admin\",\"email\":\"other@local.test\",\"password\":\"password1\"}"))
        .andExpect(status().isConflict());
  }

  /**
   *
   * Builds a JWT holding a single authority.
   *
   * @param authority the granted authority
   * @return the request post processor
   *
   */
  private static RequestPostProcessor authority(String authority) {
    return jwt().authorities(new SimpleGrantedAuthority(authority));
  }
}
