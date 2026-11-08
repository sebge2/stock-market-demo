package be.sgerard.demo.company.repository;

import be.sgerard.demo.company.model.Company;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link Company} entities.
 *
 */
public interface CompanyRepository extends JpaRepository<Company, UUID> {

  /**
   *
   * Finds a company by its commercial name.
   *
   * @param name commercial name of the company
   * @return the matching company, if it exists
   *
   */
  Optional<Company> findByName(String name);
}
