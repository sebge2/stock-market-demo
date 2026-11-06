package be.sgerard.demo.company.repository;

import be.sgerard.demo.company.model.Security;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link Security} entities.
 *
 */
public interface SecurityRepository extends JpaRepository<Security, UUID> {

  /**
   *
   * Finds a security by ISIN.
   *
   * @param isin the ISIN
   * @return the matching security, if any
   */
  Optional<Security> findByIsin(String isin);

  /**
   *
   * Finds the securities issued by a company.
   *
   * @param companyId the company identifier
   * @return the securities
   */
  List<Security> findByCompanyId(UUID companyId);
}
