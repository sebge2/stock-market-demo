package be.sgerard.demo.user.controller;

import be.sgerard.demo.user.config.OpenApiConfiguration;
import be.sgerard.demo.user.model.dto.CurrentUserDto;
import be.sgerard.demo.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Exposes the profile of the authenticated user.
 *
 */
@RestController
@RequestMapping("/profile")
@Tag(name = "Profile")
public class ProfileController {

  /** Profile operations. */
  private final ProfileService profileService;

  /** Creates the controller. */
  public ProfileController(ProfileService profileService) { this.profileService = profileService; }

  /**
   *
   * Returns the connected user.
   *
   * @param principal authenticated principal
   * @return the current user
   */
  @GetMapping
  @Operation(summary = "Returns the current connected user",
      security = @SecurityRequirement(name = OpenApiConfiguration.OAUTH2_SCHEME))
  public CurrentUserDto getCurrentUser(Principal principal) {
    return profileService.findByUsername(principal.getName());
  }
}
