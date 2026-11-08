package be.sgerard.demo.company.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import be.sgerard.demo.company.model.Company;
import be.sgerard.demo.company.model.CompanyStatus;
import be.sgerard.demo.company.model.SectorType;
import be.sgerard.demo.company.repository.CompanyRepository;
import be.sgerard.demo.company.repository.SecurityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 *
 * Verifies the security endpoints: permissions, validation, uniqueness of the ISIN and of the ticker.
 *
 */
@SpringBootTest(properties = "exchange.company-service.cors.allowed-origins=http://localhost:3000")
@AutoConfigureMockMvc
class SecurityControllerTest {

  /**
   *
   * Mock MVC entry point.
   *
   */
  @Autowired
  private MockMvc mockMvc;

  /**
   *
   * Security repository.
   *
   */
  @Autowired
  private SecurityRepository securityRepository;

  /**
   *
   * Company repository.
   *
   */
  @Autowired
  private CompanyRepository companyRepository;

  /**
   *
   * Identifier of the company issuing the test securities.
   *
   */
  private String companyId;

  /**
   *
   * Starts each test with a single company and no security.
   *
   */
  @BeforeEach
  void setUp() {
    securityRepository.deleteAll();
    companyRepository.deleteAll();
    companyId = companyRepository.save(new Company("Airbus", "NL", SectorType.INDUSTRIALS, CompanyStatus.LISTED)).getId()
        .toString();
  }

  /**
   *
   * @param authorities granted authorities
   * @return a JWT carrying the authorities
   */
  private RequestPostProcessor authorities(final String... authorities) {
    return jwt().authorities(java.util.Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toArray(GrantedAuthority[]::new));
  }

  /**
   *
   * @param ticker ticker symbol
   * @param isin ISIN code
   * @return a valid request body
   */
  private String body(final String ticker, final String isin) {
    return "{\"companyId\":\"" + companyId + "\",\"name\":\"Airbus Common Stock\",\"isin\":\"" + isin
        + "\",\"type\":\"STOCK\",\"currency\":\"EUR\",\"ticker\":\"" + ticker
        + "\",\"tradingCurrency\":\"EUR\",\"listingDate\":\"2001-06-11\",\"status\":\"LISTED\"}";
  }

  /**
   *
   * Every endpoint requires an authentication and the matching permission.
   *
   */
  @Test
  void requiresAuthenticationAndPermissions() throws Exception {
    mockMvc.perform(get("/securities")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/securities").with(authorities("COMPANY_LIST"))).andExpect(status().isForbidden());
    mockMvc.perform(post("/securities").with(authorities("SECURITY_LIST")).contentType("application/json")
        .content(body("AIR", "NL0000235190"))).andExpect(status().isForbidden());
    mockMvc.perform(delete("/securities/" + companyId).with(authorities("SECURITY_LIST")))
        .andExpect(status().isForbidden());
  }

  /**
   *
   * A security can be created, listed, updated and deleted.
   *
   */
  @Test
  void managesTheLifecycleOfASecurity() throws Exception {
    final String response = mockMvc
        .perform(post("/securities").with(authorities("SECURITY_CREATE")).contentType("application/json")
            .content(body("AIR", "NL0000235190")))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.ticker").value("AIR"))
        .andExpect(jsonPath("$.companyName").value("Airbus")).andReturn().getResponse().getContentAsString();
    final String id = response.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

    mockMvc.perform(get("/securities").with(authorities("SECURITY_LIST"))).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
    mockMvc.perform(get("/securities").param("companyId", companyId).with(authorities("SECURITY_LIST")))
        .andExpect(jsonPath("$", hasSize(1)));
    mockMvc.perform(put("/securities/" + id).with(authorities("SECURITY_UPDATE")).contentType("application/json")
        .content(body("AIRP", "NL0000235190"))).andExpect(status().isOk()).andExpect(jsonPath("$.ticker").value("AIRP"));
    mockMvc.perform(delete("/securities/" + id).with(authorities("SECURITY_DELETE"))).andExpect(status().isNoContent());
    mockMvc.perform(get("/securities/" + id).with(authorities("SECURITY_LIST"))).andExpect(status().isNotFound());
  }

  /**
   *
   * Invalid data is rejected, as well as a duplicated ISIN or ticker.
   *
   */
  @Test
  void rejectsInvalidAndDuplicatedSecurities() throws Exception {
    mockMvc.perform(post("/securities").with(authorities("SECURITY_CREATE")).contentType("application/json")
        .content(body("AIR", "not-an-isin"))).andExpect(status().isBadRequest());

    mockMvc.perform(post("/securities").with(authorities("SECURITY_CREATE")).contentType("application/json")
        .content(body("AIR", "NL0000235190"))).andExpect(status().isCreated());
    mockMvc.perform(post("/securities").with(authorities("SECURITY_CREATE")).contentType("application/json")
        .content(body("OTHER", "NL0000235190"))).andExpect(status().isConflict());
    mockMvc.perform(post("/securities").with(authorities("SECURITY_CREATE")).contentType("application/json")
        .content(body("AIR", "FR0000120073"))).andExpect(status().isConflict());
  }
}
