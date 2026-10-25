package be.sgerard.demo.user.config;

import be.sgerard.demo.user.model.Permission;
import be.sgerard.demo.user.model.PermissionCategory;
import be.sgerard.demo.user.model.Role;
import be.sgerard.demo.user.model.RoleStatus;
import be.sgerard.demo.user.repository.PermissionRepository;
import be.sgerard.demo.user.repository.RoleRepository;
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

  /**
   *
   * Creates the local authorization data initializer.
   *
   * @param roleRepository role repository
   * @param permissionRepository permission repository
   */
  public LocalDataInitializer(RoleRepository roleRepository, PermissionRepository permissionRepository) {
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
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
  }
}
