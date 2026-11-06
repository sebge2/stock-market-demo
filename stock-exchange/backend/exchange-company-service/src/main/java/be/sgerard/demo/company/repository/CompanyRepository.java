package be.sgerard.demo.company.repository;

import be.sgerard.demo.company.model.Company;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository of {@link Company} entities.
 *
 */
public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
