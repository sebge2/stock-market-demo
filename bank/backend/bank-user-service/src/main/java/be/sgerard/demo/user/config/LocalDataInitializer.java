package be.sgerard.demo.user.config;

import be.sgerard.demo.user.model.Permission;
import be.sgerard.demo.user.model.PermissionCategory;
import be.sgerard.demo.user.model.Role;
import be.sgerard.demo.user.model.RoleStatus;
import be.sgerard.demo.user.model.UserAccount;
import be.sgerard.demo.user.repository.PermissionRepository;
import be.sgerard.demo.user.repository.RoleRepository;
import be.sgerard.demo.user.repository.UserAccountRepository;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Loads authorization reference data for the local profile.
 *
 */
@Component
@Profile("local")
public class LocalDataInitializer implements ApplicationRunner {

  /** Authorization role repository. */
  private final RoleRepository roleRepository;
  /** Authorization permission repository. */
  private final PermissionRepository permissionRepository;
  /** User account repository. */
  private final UserAccountRepository userAccountRepository;
  /** Password encoder. */
  private final PasswordEncoder passwordEncoder;

  /**
   *
   * Creates the local authorization data initializer.
   *
   * @param roleRepository role repository
   * @param permissionRepository permission repository
   * @param userAccountRepository user account repository
   * @param passwordEncoder password encoder
   */
  public LocalDataInitializer(RoleRepository roleRepository, PermissionRepository permissionRepository,
      UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
    this.userAccountRepository = userAccountRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   *
   * Creates the ADMIN role and permission when absent.
   *
   * @param args startup arguments
   */
  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    Permission permission = permissionRepository.findByCode("ADMIN")
        .orElseGet(() -> permissionRepository.save(
            new Permission("ADMIN", "Administrative access", PermissionCategory.ADMIN)));
    Role role = roleRepository.findByName("ADMIN")
        .orElseGet(() -> roleRepository.save(
            new Role("ADMIN", "Administrator role", RoleStatus.ACTIVE)));
    role.addPermission(permission);
    roleRepository.save(role);

    UserAccount administrator = userAccountRepository.findByUsername("sebastien.gerard")
        .orElseGet(() -> new UserAccount(UUID.fromString("72e91c48-68b6-4de7-9a40-21b9b6224001"),
            "sebastien.gerard", "sebastien.gerard@local.test", passwordEncoder.encode("changeit")));
    administrator.addRole(role);
    userAccountRepository.save(administrator);
  }
}
