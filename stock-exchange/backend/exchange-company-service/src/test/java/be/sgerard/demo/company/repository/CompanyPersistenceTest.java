package be.sgerard.demo.company.repository;

import be.sgerard.demo.company.model.Company;
import be.sgerard.demo.company.model.CompanyStatus;
import be.sgerard.demo.company.model.Exchange;
import be.sgerard.demo.company.model.ExchangeStatus;
import be.sgerard.demo.company.model.Listing;
import be.sgerard.demo.company.model.ListingStatus;
import be.sgerard.demo.company.model.PriceInterval;
import be.sgerard.demo.company.model.Security;
import be.sgerard.demo.company.model.SecurityStatus;
import be.sgerard.demo.company.model.SecurityType;
import be.sgerard.demo.company.model.StockPrice;
import be.sgerard.demo.company.model.dto.ListingDto;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 *
 * Persistence tests of the company, security, exchange, listing and stock price entities.
 *
 */
@DataJpaTest
class CompanyPersistenceTest {

  /**
   *
   * Company repository.
   *
   */
  @Autowired
  private CompanyRepository companies;

  /**
   *
   * Security repository.
   *
   */
  @Autowired
  private SecurityRepository securities;

  /**
   *
   * Exchange repository.
   *
   */
  @Autowired
  private ExchangeRepository exchanges;

  /**
   *
   * Listing repository.
   *
   */
  @Autowired
  private ListingRepository listings;

  /**
   *
   * Stock price repository.
   *
   */
  @Autowired
  private StockPriceRepository prices;

  /**
   *
   * Entity manager used to clear the persistence context.
   *
   */
  @Autowired
  private TestEntityManager entityManager;

  /**
   *
   * Persists a valid company.
   *
   */
  private Company newCompany() {
    return companies.saveAndFlush(new Company("Apple", "US", CompanyStatus.ACTIVE));
  }

  /**
   *
   * Persists a valid security of a new company.
   *
   */
  private Security newSecurity(String isin) {
    return securities.saveAndFlush(
        new Security(newCompany(), "Apple Common Stock", isin, SecurityType.STOCK, "USD", SecurityStatus.ACTIVE));
  }

  /**
   *
   * Persists a valid exchange.
   *
   */
  private Exchange newExchange(String mic) {
    return exchanges.saveAndFlush(
        new Exchange("NASDAQ", mic, "US", "America/New_York", "USD", ExchangeStatus.ACTIVE));
  }

  /**
   *
   * Persists a valid listing.
   *
   */
  private Listing newListing(Security security, Exchange exchange, String ticker) {
    return listings.saveAndFlush(new Listing(
        security, exchange, ticker, "USD", LocalDate.of(1980, 12, 12), ListingStatus.ACTIVE, true));
  }

  /**
   *
   * Creates a valid price for a listing.
   *
   */
  private StockPrice price(Listing listing, LocalDateTime at, PriceInterval interval) {
    return new StockPrice(listing, at, new BigDecimal("10"), new BigDecimal("12"), new BigDecimal("9"),
        new BigDecimal("11"), 1000, interval);
  }

  /**
   *
   * A company gets a generated id and audit dates.
   *
   */
  @Test
  void companyIsPersistedWithGeneratedIdAndAuditDates() {
    Company saved = newCompany();

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
  }

  /**
   *
   * The modification date is refreshed on update while the creation date is kept.
   *
   */
  @Test
  void companyUpdatedAtIsRefreshedOnUpdate() throws InterruptedException {
    Company company = newCompany();
    LocalDateTime created = company.getCreatedAt();
    LocalDateTime before = company.getUpdatedAt();

    Thread.sleep(5);
    company.setWebsite("https://apple.com");
    companies.saveAndFlush(company);

    assertThat(company.getUpdatedAt()).isAfter(before);
    assertThat(company.getCreatedAt()).isEqualTo(created);
  }

  /**
   *
   * Invalid companies are rejected.
   *
   */
  @Test
  void invalidCompanyIsRejected() {
    assertThatThrownBy(() -> companies.saveAndFlush(new Company(" ", "US", CompanyStatus.ACTIVE)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> companies.saveAndFlush(new Company("Apple", "USA", CompanyStatus.ACTIVE)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> companies.saveAndFlush(new Company("Apple", "US", null)))
        .isInstanceOf(ConstraintViolationException.class);
  }

  /**
   *
   * A company can issue several securities.
   *
   */
  @Test
  void companyCanIssueSeveralSecurities() {
    Company company = newCompany();
    securities.saveAndFlush(new Security(company, "Common", "US0378331005", SecurityType.STOCK, "USD", SecurityStatus.ACTIVE));
    securities.saveAndFlush(new Security(company, "Preferred", "US0378331013", SecurityType.PREFERRED_STOCK, "USD", SecurityStatus.ACTIVE));
    entityManager.clear();

    assertThat(securities.findByCompanyId(company.getId())).hasSize(2);
    assertThat(securities.findByIsin("US0378331005")).get()
        .satisfies(s -> assertThat(s.getType()).isEqualTo(SecurityType.STOCK));
  }

  /**
   *
   * The ISIN is unique and must be well formed.
   *
   */
  @Test
  void isinMustBeUnique() {
    newSecurity("US0378331005");

    assertThatThrownBy(() -> newSecurity("US0378331005")).isInstanceOf(DataIntegrityViolationException.class);
  }

  /**
   *
   * A malformed ISIN is rejected.
   *
   */
  @Test
  void malformedIsinIsRejected() {
    assertThatThrownBy(() -> newSecurity("bad")).isInstanceOf(ConstraintViolationException.class);
  }

  /**
   *
   * A security without issuer is rejected.
   *
   */
  @Test
  void securityWithoutCompanyIsRejected() {
    assertThatThrownBy(() -> securities.saveAndFlush(
        new Security(null, "X", "US0378331005", SecurityType.STOCK, "USD", SecurityStatus.ACTIVE)))
        .isInstanceOf(ConstraintViolationException.class);
  }

  /**
   *
   * Shares counts are optional but cannot be negative.
   *
   */
  @Test
  void sharesAreOptionalButNotNegative() {
    Security security = newSecurity("US0378331005");
    assertThat(security.getIssuedShares()).isNull();

    security.setIssuedShares(-1L);
    assertThatThrownBy(() -> securities.saveAndFlush(security)).isInstanceOf(ConstraintViolationException.class);
  }

  /**
   *
   * The MIC of an exchange is unique.
   *
   */
  @Test
  void exchangeIsFoundByMicAndMicIsUnique() {
    Exchange saved = newExchange("XNAS");

    assertThat(exchanges.findByMic("XNAS")).contains(saved);
    assertThatThrownBy(() -> newExchange("XNAS")).isInstanceOf(DataIntegrityViolationException.class);
  }

  /**
   *
   * A malformed MIC is rejected.
   *
   */
  @Test
  void malformedMicIsRejected() {
    assertThatThrownBy(() -> newExchange("toolong")).isInstanceOf(ConstraintViolationException.class);
  }

  /**
   *
   * One security can be listed on several exchanges and mapped to a DTO.
   *
   */
  @Test
  void securityCanBeListedOnSeveralExchanges() {
    Security security = newSecurity("US0378331005");
    Listing nasdaq = newListing(security, newExchange("XNAS"), "AAPL");
    newListing(security, newExchange("XETR"), "APC");
    entityManager.clear();

    assertThat(listings.findBySecurityId(security.getId())).hasSize(2);
    ListingDto dto = ListingDto.from(listings.findById(nasdaq.getId()).orElseThrow());
    assertThat(dto.securityId()).isEqualTo(security.getId());
    assertThat(dto.exchangeId()).isEqualTo(nasdaq.getExchange().getId());
    assertThat(dto.isPrimary()).isTrue();
    assertThat(dto.status()).isEqualTo(ListingStatus.ACTIVE);
  }

  /**
   *
   * A ticker is unique within an exchange only.
   *
   */
  @Test
  void tickerIsUniquePerExchange() {
    Security security = newSecurity("US0378331005");
    Exchange exchange = newExchange("XNAS");
    newListing(security, exchange, "AAPL");

    assertThatThrownBy(() -> newListing(security, exchange, "AAPL"))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  /**
   *
   * The same ticker is allowed on another exchange.
   *
   */
  @Test
  void sameTickerIsAllowedOnAnotherExchange() {
    Security security = newSecurity("US0378331005");
    newListing(security, newExchange("XNAS"), "AAPL");

    assertThat(newListing(security, newExchange("XETR"), "AAPL").getId()).isNotNull();
  }

  /**
   *
   * Listing can be found by exchange and ticker and be delisted.
   *
   */
  @Test
  void listingCanBeFoundAndDelisted() {
    Exchange exchange = newExchange("XNAS");
    Listing listing = newListing(newSecurity("US0378331005"), exchange, "AAPL");

    listing.setStatus(ListingStatus.DELISTED);
    listing.setDelistingDate(LocalDate.of(2030, 1, 1));
    listings.saveAndFlush(listing);
    entityManager.clear();

    assertThat(listings.findByExchangeIdAndTicker(exchange.getId(), "AAPL")).get().satisfies(l -> {
      assertThat(l.getStatus()).isEqualTo(ListingStatus.DELISTED);
      assertThat(l.getDelistingDate()).isEqualTo(LocalDate.of(2030, 1, 1));
    });
  }

  /**
   *
   * Stock prices are persisted and queried by listing, interval and period.
   *
   */
  @Test
  void stockPricesAreQueriedByListingIntervalAndPeriod() {
    Listing listing = newListing(newSecurity("US0378331005"), newExchange("XNAS"), "AAPL");
    LocalDateTime t0 = LocalDateTime.of(2025, 1, 1, 10, 0);
    prices.saveAndFlush(price(listing, t0.plusMinutes(1), PriceInterval.ONE_MINUTE));
    prices.saveAndFlush(price(listing, t0, PriceInterval.ONE_MINUTE));
    prices.saveAndFlush(price(listing, t0, PriceInterval.ONE_DAY));
    prices.saveAndFlush(price(listing, t0.plusHours(5), PriceInterval.ONE_MINUTE));
    entityManager.clear();

    assertThat(prices.findByListingIdAndIntervalAndTimestampBetweenOrderByTimestampAsc(
        listing.getId(), PriceInterval.ONE_MINUTE, t0, t0.plusMinutes(1)))
        .extracting(StockPrice::getTimestamp)
        .containsExactly(t0, t0.plusMinutes(1));
  }

  /**
   *
   * A price is unique per listing, timestamp and interval.
   *
   */
  @Test
  void stockPriceIsUniquePerListingTimestampAndInterval() {
    Listing listing = newListing(newSecurity("US0378331005"), newExchange("XNAS"), "AAPL");
    LocalDateTime at = LocalDateTime.of(2025, 1, 1, 10, 0);
    prices.saveAndFlush(price(listing, at, PriceInterval.ONE_MINUTE));

    assertThatThrownBy(() -> prices.saveAndFlush(price(listing, at, PriceInterval.ONE_MINUTE)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  /**
   *
   * Negative prices and missing interval are rejected.
   *
   */
  @Test
  void invalidStockPriceIsRejected() {
    Listing listing = newListing(newSecurity("US0378331005"), newExchange("XNAS"), "AAPL");
    LocalDateTime at = LocalDateTime.of(2025, 1, 1, 10, 0);

    assertThatThrownBy(() -> prices.saveAndFlush(new StockPrice(listing, at, new BigDecimal("-1"),
        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 1, PriceInterval.ONE_DAY)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> prices.saveAndFlush(price(listing, at, null)))
        .isInstanceOf(ConstraintViolationException.class);
  }
}
