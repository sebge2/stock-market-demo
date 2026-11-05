package be.sgerard.demo.employee.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import be.sgerard.demo.employee.config.UserServiceConfiguration;
import be.sgerard.demo.employee.model.dto.UserServiceCreationRequestDto;
import be.sgerard.demo.employee.model.dto.UserServiceCreationResponseDto;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 *
 * Tests of UserServiceClient against a mocked HTTP server: JWT propagation and selective retry.
 *
 */
class UserServiceClientTest {

  /**
   *
   * Base URL of the mocked user-service.
   *
   */
  private static final String BASE_URL = "http://user-service";

  /**
   *
   * Value of the propagated token.
   *
   */
  private static final String TOKEN = "the-caller-token";

  /**
   *
   * Mocked HTTP server bound to the RestTemplate.
   *
   */
  private MockRestServiceServer server;

  /**
   *
   * Client under test.
   *
   */
  private UserServiceClient client;

  /**
   *
   * Builds the client with the production retry policy (without backoff) and authenticates a caller.
   *
   */
  @BeforeEach
  void setUp() {
    final RestTemplate restTemplate = new RestTemplate();
    restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));
    server = MockRestServiceServer.bindTo(restTemplate).build();

    final RetryTemplate retryTemplate = new UserServiceConfiguration().userServiceRetryTemplate();
    retryTemplate.setBackOffPolicy(new NoBackOffPolicy());
    client = new UserServiceClient(restTemplate, retryTemplate);

    final Jwt jwt = new Jwt(TOKEN, Instant.now(), Instant.now().plusSeconds(60),
        Map.of("alg", "none"), Map.of("sub", "caller"));
    SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
  }

  /**
   *
   * Clears the security context.
   *
   */
  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  /**
   *
   * Builds a creation request.
   *
   * @return the request
   *
   */
  private UserServiceCreationRequestDto request() {
    return new UserServiceCreationRequestDto("john.doe", "john.doe@example.com", "Secret123!",
        new LinkedHashSet<>());
  }

  /**
   *
   * Builds the JSON body of a successful creation.
   *
   * @param id the created user identifier
   * @return the JSON body
   *
   */
  private String createdBody(UUID id) {
    return "{\"id\":\"" + id + "\",\"username\":\"john.doe\",\"email\":\"john.doe@example.com\"}";
  }

  /**
   *
   * The bearer token of the caller is forwarded and the response is returned.
   *
   */
  @Test
  void forwardsBearerTokenOfCaller() {
    final UUID id = UUID.randomUUID();
    server.expect(ExpectedCount.once(), requestTo(BASE_URL + "/users"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(header("Authorization", "Bearer " + TOKEN))
        .andRespond(withSuccess(createdBody(id), MediaType.APPLICATION_JSON));

    final UserServiceCreationResponseDto response = client.createUser(request());

    assertEquals(id, response.getId());
    server.verify();
  }

  /**
   *
   * Without a JWT in the security context no call is made.
   *
   */
  @Test
  void failsWithoutJwtAndDoesNotCallUserService() {
    SecurityContextHolder.clearContext();

    assertThrows(IllegalStateException.class, () -> client.createUser(request()));
    server.verify();
  }

  /**
   *
   * A 5xx is retried and the call succeeds as soon as the user-service recovers.
   *
   */
  @Test
  void retriesServerErrorsThenSucceeds() {
    final UUID id = UUID.randomUUID();
    server.expect(ExpectedCount.times(2), requestTo(BASE_URL + "/users"))
        .andRespond(withServerError());
    server.expect(ExpectedCount.once(), requestTo(BASE_URL + "/users"))
        .andRespond(withSuccess(createdBody(id), MediaType.APPLICATION_JSON));

    assertEquals(id, client.createUser(request()).getId());
    server.verify();
  }

  /**
   *
   * A persistent 5xx is attempted exactly MAX_ATTEMPTS times and the original exception is thrown.
   *
   */
  @Test
  void givesUpAfterMaxAttemptsOnServerError() {
    server.expect(ExpectedCount.times(UserServiceConfiguration.MAX_ATTEMPTS),
        requestTo(BASE_URL + "/users")).andRespond(withServerError());

    assertThrows(HttpServerErrorException.class, () -> client.createUser(request()));
    server.verify();
  }

  /**
   *
   * A 4xx is never retried.
   *
   */
  @Test
  void doesNotRetryClientErrors() {
    server.expect(ExpectedCount.once(), requestTo(BASE_URL + "/users"))
        .andRespond(withBadRequest());

    assertThrows(HttpClientErrorException.class, () -> client.createUser(request()));
    server.verify();
  }

  /**
   *
   * A 409 conflict is not retried either (the user may already exist).
   *
   */
  @Test
  void doesNotRetryConflict() {
    server.expect(ExpectedCount.once(), requestTo(BASE_URL + "/users"))
        .andRespond(withStatus(HttpStatus.CONFLICT));

    final HttpClientErrorException exception =
        assertThrows(HttpClientErrorException.class, () -> client.createUser(request()));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    server.verify();
  }

  /**
   *
   * I/O errors and timeouts are retried, then reported as ResourceAccessException.
   *
   */
  @Test
  void retriesIoErrors() {
    server.expect(ExpectedCount.times(UserServiceConfiguration.MAX_ATTEMPTS),
        requestTo(BASE_URL + "/users")).andRespond(request -> {
          throw new IOException("Read timed out");
        });

    assertThrows(ResourceAccessException.class, () -> client.createUser(request()));
    server.verify();
  }

  /**
   *
   * The user deletion forwards the token and targets the user identifier.
   *
   */
  @Test
  void deletesUserWithBearerToken() {
    final UUID id = UUID.randomUUID();
    server.expect(ExpectedCount.once(), requestTo(BASE_URL + "/users/" + id))
        .andExpect(method(HttpMethod.DELETE))
        .andExpect(header("Authorization", "Bearer " + TOKEN))
        .andRespond(withStatus(HttpStatus.NO_CONTENT));

    client.deleteUser(id);

    server.verify();
  }
}
