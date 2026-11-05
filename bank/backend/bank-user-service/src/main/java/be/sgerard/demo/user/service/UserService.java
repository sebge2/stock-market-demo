package be.sgerard.demo.user.service;

import be.sgerard.demo.user.mapper.UserMapper;
import be.sgerard.demo.user.model.Role;
import be.sgerard.demo.user.model.UserAccount;
import be.sgerard.demo.user.model.dto.UserDto;
import be.sgerard.demo.user.model.dto.UserRequestDto;
import be.sgerard.demo.user.repository.RoleRepository;
import be.sgerard.demo.user.repository.UserAccountRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * Provides CRUD operations for user accounts and their roles.
 *
 */
@Service
@Transactional(readOnly = true)
public class UserService {

  /**
   *
   * User account persistence.
   *
   */
  private final UserAccountRepository userAccountRepository;

  /**
   *
   * Role persistence.
   *
   */
  private final RoleRepository roleRepository;

  /**
   *
   * API mapper.
   *
   */
  private final UserMapper userMapper;

  /**
   *
   * Password hashing.
   *
   */
  private final PasswordEncoder passwordEncoder;

  /**
   *
   * Avatar reduction.
   *
   */
  private final AvatarProcessor avatarProcessor;

  /**
   *
   * Creates the service.
   *
   * @param userAccountRepository user account persistence
   * @param roleRepository role persistence
   * @param userMapper API mapper
   * @param passwordEncoder password encoder
   * @param avatarProcessor avatar reduction
   *
   */
  public UserService(UserAccountRepository userAccountRepository, RoleRepository roleRepository,
      UserMapper userMapper, PasswordEncoder passwordEncoder, AvatarProcessor avatarProcessor) {
    this.avatarProcessor = avatarProcessor;
    this.userAccountRepository = userAccountRepository;
    this.roleRepository = roleRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   *
   * @return all users
   *
   */
  public List<UserDto> findAll() {
    return userAccountRepository.findAll().stream().map(userMapper::toDto).toList();
  }

  /**
   *
   * @param id user identifier
   * @return the user
   *
   */
  public UserDto findById(UUID id) {
    return userMapper.toDto(findEntity(id));
  }

  /**
   *
   * Creates a user; the password is mandatory.
   *
   * @param request creation data
   * @return the created user
   *
   */
  @Transactional
  public UserDto create(UserRequestDto request) {
    if (request.getPassword() == null || request.getPassword().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
    }
    checkUnique(request, null);
    UserAccount account = userMapper.toEntity(request, passwordEncoder.encode(request.getPassword()));
    replaceRoles(account, request.getRoleIds());
    applyAvatar(account, request.getAvatar());
    return userMapper.toDto(userAccountRepository.save(account));
  }

  /**
   *
   * Updates a user; the password is kept when the request has none.
   *
   * @param id user identifier
   * @param request replacement data
   * @return the updated user
   *
   */
  @Transactional
  public UserDto update(UUID id, UserRequestDto request) {
    UserAccount account = findEntity(id);
    checkUnique(request, id);
    userMapper.updateEntity(request, account);
    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    }
    replaceRoles(account, request.getRoleIds());
    applyAvatar(account, request.getAvatar());
    return userMapper.toDto(userAccountRepository.save(account));
  }

  /**
   *
   * Deletes a user.
   *
   * @param id user identifier
   *
   */
  @Transactional
  public void delete(UUID id) {
    userAccountRepository.delete(findEntity(id));
  }

  /**
   *
   * Stores the reduced avatar; a {@code null} request keeps the current one and a blank request removes it.
   *
   */
  private void applyAvatar(UserAccount account, String avatar) {
    if (avatar == null) {
      return;
    }
    account.setAvatar(avatar.isBlank() ? null : avatarProcessor.process(avatar));
  }

  /**
   *
   * Rejects a duplicated username or email.
   *
   */
  private void checkUnique(UserRequestDto request, UUID currentId) {
    userAccountRepository.findByUsername(request.getUsername())
        .filter(existing -> !existing.getId().equals(currentId))
        .ifPresent(existing -> {
          throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists: " + request.getUsername());
        });
    userAccountRepository.findByEmail(request.getEmail())
        .filter(existing -> !existing.getId().equals(currentId))
        .ifPresent(existing -> {
          throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists: " + request.getEmail());
        });
  }

  /**
   *
   * Replaces the roles of an account with the requested set.
   *
   */
  private void replaceRoles(UserAccount account, Set<UUID> roleIds) {
    Set<Role> roles = new LinkedHashSet<>();
    for (UUID roleId : roleIds) {
      roles.add(roleRepository.findById(roleId).orElseThrow(
          () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + roleId)));
    }
    new LinkedHashSet<>(account.getRoles()).forEach(account::removeRole);
    roles.forEach(account::addRole);
  }

  /**
   *
   * Finds an account or raises a not-found response.
   *
   */
  private UserAccount findEntity(UUID id) {
    return userAccountRepository.findById(id).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
  }
}
