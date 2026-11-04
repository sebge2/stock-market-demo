package be.sgerard.demo.user.mapper;

import be.sgerard.demo.user.model.Role;
import be.sgerard.demo.user.model.UserAccount;
import be.sgerard.demo.user.model.dto.UserDto;
import be.sgerard.demo.user.model.dto.UserRequestDto;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 *
 * Maps user account entities to their API representations.
 *
 */
@Component
public class UserMapper {

    /**
     *
     * Creates an entity from a request.
     *
     * @param request      creation data
     * @param passwordHash already hashed password
     * @return the new account, with a generated identifier
     *
     */
    public UserAccount toEntity(UserRequestDto request, String passwordHash) {
        return new UserAccount(UUID.randomUUID(), request.getUsername(), request.getEmail(), passwordHash);
    }

    /**
     *
     * Copies the mutable request data (except the password and the roles) to an entity.
     *
     * @param request update data
     * @param account account to update
     *
     */
    public void updateEntity(UserRequestDto request, UserAccount account) {
        account.setUsername(request.getUsername());
        account.setEmail(request.getEmail());
    }

    /**
     *
     * Converts an entity to a response.
     *
     * @param account the account
     * @return its public representation
     *
     */
    public UserDto toDto(UserAccount account) {
        final UserDto dto = new UserDto();
        dto.setId(account.getId());
        dto.setUsername(account.getUsername());
        dto.setEmail(account.getEmail());
        dto.setRoleIds(account.getRoles().stream().map(Role::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        return dto;
    }
}
