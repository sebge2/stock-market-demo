package be.sgerard.demo.exchange.user.controller;

import be.sgerard.demo.exchange.user.config.OpenApiConfiguration;
import be.sgerard.demo.exchange.user.model.dto.UserDto;
import be.sgerard.demo.exchange.user.model.dto.UserRequestDto;
import be.sgerard.demo.exchange.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Exposes CRUD endpoints for user accounts and their roles.
 *
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Management of users")
@SecurityRequirement(name = OpenApiConfiguration.OAUTH2_SCHEME)
public class UserController {

  /**
   *
   * User operations.
   *
   */
  private final UserService userService;

  /**
   *
   * Creates the controller.
   *
   * @param userService user operations
   *
   */
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   *
   * @return all users
   *
   */
  @GetMapping
  @Operation(summary = "List the users", description = "Requires the USER_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The list of users"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the USER_LIST permission")
  })
  @PreAuthorize("hasAuthority('USER_LIST')")
  public List<UserDto> findAll() {
    return userService.findAll();
  }

  /**
   *
   * @param id user identifier
   * @return the user
   *
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get a user by identifier", description = "Requires the USER_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The user"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the USER_LIST permission"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PreAuthorize("hasAuthority('USER_LIST')")
  public UserDto findById(@PathVariable UUID id) {
    return userService.findById(id);
  }

  /**
   *
   * @param request creation data
   * @return the created user
   *
   */
  @PostMapping
  @Operation(summary = "Create a user", description = "Requires the USER_CREATE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "User created"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the USER_CREATE permission"),
      @ApiResponse(responseCode = "404", description = "A referenced role was not found"),
      @ApiResponse(responseCode = "409", description = "User already exists")
  })
  @PreAuthorize("hasAuthority('USER_CREATE')")
  public ResponseEntity<UserDto> create(@Valid @RequestBody UserRequestDto request) {
    UserDto user = userService.create(request);
    return ResponseEntity.created(URI.create("/users/" + user.getId())).body(user);
  }

  /**
   *
   * @param id user identifier
   * @param request replacement data
   * @return the updated user
   *
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update a user", description = "Requires the USER_UPDATE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "User updated"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the USER_UPDATE permission"),
      @ApiResponse(responseCode = "404", description = "User not found"),
      @ApiResponse(responseCode = "409", description = "User already exists")
  })
  @PreAuthorize("hasAuthority('USER_UPDATE')")
  public UserDto update(@PathVariable UUID id, @Valid @RequestBody UserRequestDto request) {
    return userService.update(id, request);
  }

  /**
   *
   * @param id user identifier
   *
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a user", description = "Requires the USER_DELETE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "User deleted"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the USER_DELETE permission"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PreAuthorize("hasAuthority('USER_DELETE')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    userService.delete(id);
  }
}
