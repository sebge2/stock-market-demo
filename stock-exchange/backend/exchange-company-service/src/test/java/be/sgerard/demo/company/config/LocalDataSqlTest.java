package be.sgerard.demo.company.config;

import static org.assertj.core.api.Assertions.assertThat;

import be.sgerard.demo.company.model.CompanyStatus;
import be.sgerard.demo.company.model.SectorType;
import be.sgerard.demo.company.repository.CompanyRepository;
import be.sgerard.demo.company.repository.SecurityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Verifies the local company reference data.
 *
 */
@SpringBootTest
@ActiveProfiles("local")
@Transactional
class LocalDataSqlTest {

  /**
   *
   * Company persistence.
   *
   */
  @Autowired
  private CompanyRepository companyRepository;

  /**
   *
   * Security persistence.
   *
   */
  @Autowired
  private SecurityRepository securityRepository;

  /**
   *
   * Verifies that the local profile loads the CAC 40 companies and their securities.
   *
   */
  @Test
  void loadsCac40Companies() {
    assertThat(companyRepository.count()).isEqualTo(40);
    assertThat(companyRepository.findByName("TotalEnergies").orElseThrow().getSectorType())
        .isEqualTo(SectorType.ENERGY);
    assertThat(companyRepository.findByName("TotalEnergies").orElseThrow().getStatus())
        .isEqualTo(CompanyStatus.LISTED);
    assertThat(securityRepository.count()).isEqualTo(40);
    assertThat(securityRepository.findByTicker("TTE")).isPresent();
  }
}
