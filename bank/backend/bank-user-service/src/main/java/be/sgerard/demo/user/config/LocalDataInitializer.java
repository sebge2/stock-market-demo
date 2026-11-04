package be.sgerard.demo.user.config;

import be.sgerard.demo.user.model.Permission;
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
   * Creates the ADMIN role, its permissions and the administrator account when absent.
   *
   * @param args startup arguments
   */
  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    Role role = roleRepository.findByName("ADMIN")
        .orElseGet(() -> roleRepository.save(
            new Role("ADMIN", "Administrator role", RoleStatus.ACTIVE)));
    role.addPermission(findOrCreatePermission("ADMIN", "Administrative access"));
    role.addPermission(findOrCreatePermission("ROLE_LIST", "List and read roles"));
    role.addPermission(findOrCreatePermission("ROLE_CREATE", "Create roles"));
    role.addPermission(findOrCreatePermission("ROLE_UPDATE", "Update roles"));
    role.addPermission(findOrCreatePermission("ROLE_DELETE", "Delete roles"));
    role.addPermission(findOrCreatePermission("PERMISSION_LIST", "List and read permissions"));
    role.addPermission(findOrCreatePermission("PERMISSION_CREATE", "Create permissions"));
    role.addPermission(findOrCreatePermission("PERMISSION_UPDATE", "Update permissions"));
    role.addPermission(findOrCreatePermission("PERMISSION_DELETE", "Delete permissions"));
    role.addPermission(findOrCreatePermission("USER_LIST", "List and read users"));
    role.addPermission(findOrCreatePermission("USER_CREATE", "Create users"));
    role.addPermission(findOrCreatePermission("USER_UPDATE", "Update users"));
    role.addPermission(findOrCreatePermission("USER_DELETE", "Delete users"));
    roleRepository.save(role);

    UserAccount administrator = userAccountRepository.findByUsername("sebastien.gerard")
        .orElseGet(() -> new UserAccount(UUID.fromString("72e91c48-68b6-4de7-9a40-21b9b6224001"),
            "sebastien.gerard", "sebastien.gerard@local.test", passwordEncoder.encode("changeit")));
    administrator.addRole(role);
    userAccountRepository.save(administrator);
  }

  /**
   *
   * Returns the permission with the given code, creating it when absent.
   *
   * @param code permission code
   * @param description permission description
   * @return the existing or created permission
   */
  private Permission findOrCreatePermission(String code, String description) {
    return permissionRepository.findByCode(code)
        .orElseGet(() -> permissionRepository.save(new Permission(code, description)));
  }
}
