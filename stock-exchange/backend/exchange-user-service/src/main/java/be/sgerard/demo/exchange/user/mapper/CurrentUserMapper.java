package be.sgerard.demo.exchange.user.mapper;

import be.sgerard.demo.exchange.user.model.Permission;
import be.sgerard.demo.exchange.user.model.UserAccount;
import be.sgerard.demo.exchange.user.model.dto.CurrentUserDto;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
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
   * @return the DTO exposing the username, the full name, the email and the names of the granted permissions
   */
  public CurrentUserDto toDto(UserAccount account) {
    final CurrentUserDto dto = new CurrentUserDto();
    dto.setUsername(account.getUsername());
    dto.setFullName(account.getFullName());
    dto.setEmail(account.getEmail());
    dto.setAvatar(account.getAvatar());
    dto.setPermissions(account.getRoles().stream().flatMap(role -> role.getPermissions().stream())
        .map(Permission::getCode).sorted(Comparator.naturalOrder())
        .collect(Collectors.toCollection(LinkedHashSet::new)));
    return dto;
  }
}
