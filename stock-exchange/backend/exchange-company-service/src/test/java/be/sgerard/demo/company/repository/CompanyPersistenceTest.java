package be.sgerard.demo.company.repository;

import be.sgerard.demo.company.model.Company;
import be.sgerard.demo.company.model.CompanyStatus;
import be.sgerard.demo.company.model.PriceInterval;
import be.sgerard.demo.company.model.Security;
import be.sgerard.demo.company.model.SecurityStatus;
import be.sgerard.demo.company.model.SecurityType;
import be.sgerard.demo.company.model.SectorType;
import be.sgerard.demo.company.model.StockPrice;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 *
 * Persistence tests for companies, securities and their market prices.
 *
 */
@DataJpaTest
class CompanyPersistenceTest {

  /** Company repository. */
  @Autowired
  private CompanyRepository companies;

  /** Security repository. */
  @Autowired
  private SecurityRepository securities;

  /** Stock price repository. */
  @Autowired
  private StockPriceRepository prices;

  /**
   *
   * Persists a valid company.
   *
   * @return the persisted company
   */
  private Company newCompany() {
    return companies.saveAndFlush(new Company("Apple", "US", SectorType.INFORMATION_TECHNOLOGY,
        CompanyStatus.LISTED));
  }

  /**
   *
   * Persists a valid security for a new company.
   *
   * @param isin international securities identification number
   * @param ticker exchange ticker
   * @return the persisted security
   */
  private Security newSecurity(String isin, String ticker) {
    return securities.saveAndFlush(new Security(newCompany(), "Apple Common Stock", isin, SecurityType.STOCK,
        "USD", ticker, "USD", LocalDate.of(1980, 12, 12), SecurityStatus.LISTED));
  }

  /**
   *
   * Builds a valid stock price.
   *
   * @param security concerned security
   * @param timestamp start of the interval
   * @param interval price interval
   * @return a stock price
   */
  private StockPrice price(Security security, LocalDateTime timestamp, PriceInterval interval) {
    return new StockPrice(security, timestamp, new BigDecimal("10"), new BigDecimal("12"), new BigDecimal("9"),
        new BigDecimal("11"), 1000, interval);
  }

  /**
   *
   * A company gets a generated identifier and audit dates.
   *
   */
  @Test
  void companyIsPersistedWithGeneratedIdAndAuditDates() {
    final Company saved = newCompany();

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getSectorType()).isEqualTo(SectorType.INFORMATION_TECHNOLOGY);
    assertThat(saved.getStatus()).isEqualTo(CompanyStatus.LISTED);
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
  }

  /**
   *
   * Invalid companies are rejected.
   *
   */
  @Test
  void invalidCompanyIsRejected() {
    assertThatThrownBy(() -> companies.saveAndFlush(new Company(" ", "US", SectorType.INFORMATION_TECHNOLOGY,
        CompanyStatus.LISTED))).isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> companies.saveAndFlush(new Company("Apple", "USA", SectorType.INFORMATION_TECHNOLOGY,
        CompanyStatus.LISTED))).isInstanceOf(ConstraintViolationException.class);
  }

  /**
   *
   * A company can issue several securities.
   *
   */
  @Test
  void companyCanIssueSeveralSecurities() {
    final Company company = newCompany();
    securities.saveAndFlush(new Security(company, "Common", "US0378331005", SecurityType.STOCK, "USD", "AAPL",
        "USD", LocalDate.of(1980, 12, 12), SecurityStatus.LISTED));
    securities.saveAndFlush(new Security(company, "Preferred", "US0378331013", SecurityType.PREFERRED_STOCK,
        "USD", "AAPL-P", "USD", LocalDate.of(1980, 12, 12), SecurityStatus.ADMISSION));

    assertThat(securities.findByCompanyId(company.getId())).hasSize(2);
    assertThat(securities.findByIsin("US0378331005")).isPresent();
  }

  /**
   *
   * A security carries its single-market trading data and is found by ticker.
   *
   */
  @Test
  void securityStoresTradingDataAndIsFoundByTicker() {
    final Security security = newSecurity("US0378331005", "AAPL");
    security.setStatus(SecurityStatus.DELISTED);
    security.setDelistingDate(LocalDate.of(2030, 1, 1));
    security.updateCurrentPrice(new BigDecimal("187.25"), LocalDateTime.of(2025, 1, 1, 10, 0));
    securities.saveAndFlush(security);

    assertThat(securities.findByTicker("AAPL")).get().satisfies(saved -> {
      assertThat(saved.getTradingCurrency()).isEqualTo("USD");
      assertThat(saved.getListingDate()).isEqualTo(LocalDate.of(1980, 12, 12));
      assertThat(saved.getStatus()).isEqualTo(SecurityStatus.DELISTED);
      assertThat(saved.getDelistingDate()).isEqualTo(LocalDate.of(2030, 1, 1));
      assertThat(saved.getCurrentPrice()).isEqualByComparingTo("187.25");
      assertThat(saved.getCurrentPriceUpdatedAt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0));
    });
  }

  /**
   *
   * ISINs and tickers are unique on the single exchange.
   *
   */
  @Test
  void isinAndTickerMustBeUnique() {
    newSecurity("US0378331005", "AAPL");

    assertThatThrownBy(() -> newSecurity("US0378331005", "AAPL2"))
        .isInstanceOf(DataIntegrityViolationException.class);
    assertThatThrownBy(() -> newSecurity("US0378331013", "AAPL"))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  /**
   *
   * Required security trading data is validated.
   *
   */
  @Test
  void invalidSecurityIsRejected() {
    final Company company = newCompany();
    assertThatThrownBy(() -> securities.saveAndFlush(new Security(company, "Common", "US0378331005",
        SecurityType.STOCK, "USD", " ", "USD", LocalDate.of(1980, 12, 12), SecurityStatus.LISTED)))
        .isInstanceOf(ConstraintViolationException.class);
    assertThatThrownBy(() -> securities.saveAndFlush(new Security(company, "Common", "US0378331005",
        SecurityType.STOCK, "USD", "AAPL", "USD", null, SecurityStatus.LISTED)))
        .isInstanceOf(ConstraintViolationException.class);
  }

  /**
   *
   * Stock prices are queried by security, interval and period.
   *
   */
  @Test
  void stockPricesAreQueriedBySecurityIntervalAndPeriod() {
    final Security security = newSecurity("US0378331005", "AAPL");
    final LocalDateTime start = LocalDateTime.of(2025, 1, 1, 10, 0);
    prices.saveAndFlush(price(security, start.plusMinutes(1), PriceInterval.ONE_MINUTE));
    prices.saveAndFlush(price(security, start, PriceInterval.ONE_MINUTE));
    prices.saveAndFlush(price(security, start, PriceInterval.ONE_DAY));

    assertThat(prices.findBySecurityIdAndIntervalAndTimestampBetweenOrderByTimestampAsc(
        security.getId(), PriceInterval.ONE_MINUTE, start, start.plusMinutes(1)))
        .extracting(StockPrice::getTimestamp)
        .containsExactly(start, start.plusMinutes(1));
  }

  /**
   *
   * A stock price is unique for a security, timestamp and interval.
   *
   */
  @Test
  void stockPriceIsUniquePerSecurityTimestampAndInterval() {
    final Security security = newSecurity("US0378331005", "AAPL");
    final LocalDateTime timestamp = LocalDateTime.of(2025, 1, 1, 10, 0);
    prices.saveAndFlush(price(security, timestamp, PriceInterval.ONE_MINUTE));

    assertThatThrownBy(() -> prices.saveAndFlush(price(security, timestamp, PriceInterval.ONE_MINUTE)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}
