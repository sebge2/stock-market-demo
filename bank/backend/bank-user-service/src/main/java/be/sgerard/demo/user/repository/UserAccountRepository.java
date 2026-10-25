package be.sgerard.demo.user.repository;

import be.sgerard.demo.user.model.UserAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Repository for authorization-server user accounts.
 *
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

  /**
   *
   * Finds an account by login name.
   *
   * @param username login name
   * @return matching account, if present
   */
  Optional<UserAccount> findByUsername(String username);
}
