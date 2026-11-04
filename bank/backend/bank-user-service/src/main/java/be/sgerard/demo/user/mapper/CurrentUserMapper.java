package be.sgerard.demo.user.mapper;

import be.sgerard.demo.user.model.UserAccount;
import be.sgerard.demo.user.model.dto.CurrentUserDto;
import org.springframework.stereotype.Component;

/**
 *
 * Maps user accounts to the current-user representation.
 *
 */
@Component
public class CurrentUserMapper {

  /**
   *
   * Converts an account to its current-user representation.
   *
   * @param account user account
   * @return the DTO exposing the username and email only
   */
  public CurrentUserDto toDto(UserAccount account) {
    final CurrentUserDto dto = new CurrentUserDto();
    dto.setUsername(account.getUsername());
    dto.setEmail(account.getEmail());
    return dto;
  }
}
