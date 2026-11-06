package be.sgerard.demo.exchange.user.service;

import be.sgerard.demo.exchange.user.mapper.CurrentUserMapper;
import be.sgerard.demo.exchange.user.model.dto.CurrentUserDto;
import be.sgerard.demo.exchange.user.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Provides the profile of authenticated users.
 *
 */
@Service
@Transactional(readOnly = true)
public class ProfileService {

  /** User account persistence. */
  private final UserAccountRepository userAccountRepository;
  /** API mapper. */
  private final CurrentUserMapper currentUserMapper;

  /** Creates the service. */
  public ProfileService(UserAccountRepository userAccountRepository, CurrentUserMapper currentUserMapper) {
    this.userAccountRepository = userAccountRepository;
    this.currentUserMapper = currentUserMapper;
  }

  /**
   *
   * Finds the profile of a user.
   *
   * @param username login name of the authenticated user
   * @return the current user
   */
  public CurrentUserDto findByUsername(String username) {
    return userAccountRepository.findByUsername(username).map(currentUserMapper::toDto)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown user"));
  }
}
