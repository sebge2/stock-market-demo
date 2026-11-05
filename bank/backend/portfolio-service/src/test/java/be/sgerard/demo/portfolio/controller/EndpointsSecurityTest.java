package be.sgerard.demo.portfolio.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 *
 * Verifies that the portfolio endpoints are secured by permissions, documented and behave as expected.
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EndpointsSecurityTest {

  /**
   *
   * Identifier of the local portfolio holding positions.
   *
   */
  private static final String PORTFOLIO_WITH_POSITIONS = "00000000-0000-4000-e000-000000000001";

  /**
   *
   * Identifier of the local portfolio without position.
   *
   */
  private static final String EMPTY_PORTFOLIO = "00000000-0000-4000-e000-000000000002";

  /**
   *
   * Client owning the first local portfolio.
   *
   */
  private static final String CLIENT_WITH_PORTFOLIO = "00000000-0000-4000-d000-000000000001";

  /**
   *
   * Mock MVC entry point.
   *
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   *
   * Anonymous callers are rejected on every endpoint.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void anonymousIsUnauthorized() throws Exception {
    final String id = UUID.randomUUID().toString();

    mockMvc.perform(get("/portfolios")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/portfolios/" + id)).andExpect(status().isUnauthorized());
    mockMvc.perform(post("/portfolios").contentType("application/json").content(body(UUID.randomUUID())))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(put("/portfolios/" + id).contentType("application/json").content(body(UUID.randomUUID())))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(delete("/portfolios/" + id)).andExpect(status().isUnauthorized());
  }

  /**
   *
   * Callers without the matching permission are forbidden on every endpoint.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void withoutMatchingPermissionIsForbidden() throws Exception {
    final String id = UUID.randomUUID().toString();
    final String body = body(UUID.randomUUID());

    mockMvc.perform(get("/portfolios").with(authority("SCOPE_portfolio.read"))).andExpect(status().isForbidden());
    mockMvc.perform(get("/portfolios").with(authority("PORTFOLIO_CREATE"))).andExpect(status().isForbidden());
    mockMvc.perform(get("/portfolios/" + id).with(authority("CLIENT_LIST"))).andExpect(status().isForbidden());
    mockMvc.perform(post("/portfolios").with(authority("PORTFOLIO_LIST")).contentType("application/json")
        .content(body)).andExpect(status().isForbidden());
    mockMvc.perform(put("/portfolios/" + id).with(authority("PORTFOLIO_LIST")).contentType("application/json")
        .content(body)).andExpect(status().isForbidden());
    mockMvc.perform(delete("/portfolios/" + id).with(authority("PORTFOLIO_UPDATE")))
        .andExpect(status().isForbidden());
  }

  /**
   *
   * Reading with the permission returns the portfolios with their positions, purchases and receipts.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void listsAndReadsPortfolios() throws Exception {
    mockMvc.perform(get("/portfolios").with(authority("PORTFOLIO_LIST"))).andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2));
    mockMvc.perform(get("/portfolios/" + PORTFOLIO_WITH_POSITIONS).with(authority("PORTFOLIO_LIST")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.clientId").value(CLIENT_WITH_PORTFOLIO))
        .andExpect(jsonPath("$.positions.length()").value(2))
        .andExpect(jsonPath("$.positions[?(@.stockSymbol=='APPLE')].purchases[0].receipt.receiptNumber")
            .value("RCP-20260101-0001"));
    mockMvc.perform(get("/portfolios/" + UUID.randomUUID()).with(authority("PORTFOLIO_LIST")))
        .andExpect(status().isNotFound());
  }

  /**
   *
   * Invalid payloads and pagination parameters are rejected.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void invalidInputIsRejected() throws Exception {
    mockMvc.perform(post("/portfolios").with(authority("PORTFOLIO_CREATE")).contentType("application/json")
        .content("{}")).andExpect(status().isBadRequest());
    mockMvc.perform(get("/portfolios?size=0").with(authority("PORTFOLIO_LIST"))).andExpect(status().isBadRequest());
  }

  /**
   *
   * A portfolio can be created, updated and deleted, with conflicts reported as 409.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void createsUpdatesAndDeletesPortfolio() throws Exception {
    final UUID clientId = UUID.randomUUID();
    final String location = mockMvc.perform(post("/portfolios").with(authority("PORTFOLIO_CREATE"))
        .contentType("application/json").content(body(clientId)))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.clientId").value(clientId.toString()))
        .andReturn().getResponse().getHeader("Location");

    mockMvc.perform(post("/portfolios").with(authority("PORTFOLIO_CREATE")).contentType("application/json")
        .content(body(clientId))).andExpect(status().isConflict());

    final UUID newClientId = UUID.randomUUID();
    mockMvc.perform(put(location).with(authority("PORTFOLIO_UPDATE")).contentType("application/json")
        .content(body(newClientId))).andExpect(status().isOk())
        .andExpect(jsonPath("$.clientId").value(newClientId.toString()));
    mockMvc.perform(put(location).with(authority("PORTFOLIO_UPDATE")).contentType("application/json")
        .content(body(UUID.fromString(CLIENT_WITH_PORTFOLIO)))).andExpect(status().isConflict());
    mockMvc.perform(put("/portfolios/" + UUID.randomUUID()).with(authority("PORTFOLIO_UPDATE"))
        .contentType("application/json").content(body(newClientId))).andExpect(status().isNotFound());

    mockMvc.perform(delete(location).with(authority("PORTFOLIO_DELETE"))).andExpect(status().isNoContent());
    mockMvc.perform(delete(location).with(authority("PORTFOLIO_DELETE"))).andExpect(status().isNotFound());
  }

  /**
   *
   * A portfolio still holding positions cannot be deleted, an empty one can.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void deletionOfPortfolioWithPositionsIsConflict() throws Exception {
    mockMvc.perform(delete("/portfolios/" + PORTFOLIO_WITH_POSITIONS).with(authority("PORTFOLIO_DELETE")))
        .andExpect(status().isConflict());
    mockMvc.perform(delete("/portfolios/" + EMPTY_PORTFOLIO).with(authority("PORTFOLIO_DELETE")))
        .andExpect(status().isNoContent());
  }

  /**
   *
   * The API documentation is public and declares the OAuth2 flow of the user service.
   *
   * @throws Exception on failure
   *
   */
  @Test
  void documentsOAuth2Flow() throws Exception {
    mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
        .andExpect(jsonPath("$.components.securitySchemes.oauth2.flows.authorizationCode.authorizationUrl")
            .value("http://localhost:9000/oauth2/authorize"))
        .andExpect(jsonPath("$.components.securitySchemes.oauth2.flows.authorizationCode.scopes['portfolio.read']")
            .exists())
        .andExpect(jsonPath("$.components.schemas.Portfolio").exists())
        .andExpect(jsonPath("$.components.schemas.PortfolioRequest").exists())
        .andExpect(jsonPath("$.components.schemas.Position").exists())
        .andExpect(jsonPath("$.components.schemas.Purchase").exists())
        .andExpect(jsonPath("$.components.schemas.PurchaseReceipt").exists())
        .andExpect(jsonPath("$.components.schemas.PortfolioDto").doesNotExist());
  }

  /**
   *
   * Builds a request body.
   *
   * @param clientId the client identifier
   * @return the JSON body
   *
   */
  private static String body(UUID clientId) {
    return "{\"clientId\":\"" + clientId + "\"}";
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
