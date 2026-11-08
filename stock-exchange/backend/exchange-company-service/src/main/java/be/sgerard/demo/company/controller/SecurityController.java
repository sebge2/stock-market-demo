package be.sgerard.demo.company.controller;

import be.sgerard.demo.company.model.dto.SecurityDto;
import be.sgerard.demo.company.model.dto.SecurityRequestDto;
import be.sgerard.demo.company.service.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * REST controller managing the securities traded on the stock exchange. Every endpoint requires a permission.
 *
 */
@RestController
@RequestMapping("/securities")
@Tag(name = "Securities", description = "Manage the securities traded on the stock exchange")
@SecurityRequirement(name = "OAuth2")
public class SecurityController {

  /**
   *
   * Security service.
   *
   */
  private final SecurityService securityService;

  /**
   *
   * Creates the controller.
   *
   * @param securityService the security service
   */
  public SecurityController(final SecurityService securityService) {
    this.securityService = securityService;
  }

  /**
   *
   * @param companyId identifier of the issuing company, to restrict the list
   * @return the securities
   */
  @GetMapping
  @PreAuthorize("hasAuthority('SECURITY_LIST')")
  @Operation(summary = "List the securities", description = "Requires the SECURITY_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The securities"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the SECURITY_LIST permission")
  })
  public List<SecurityDto> findAll(
      @Parameter(description = "Restricts the list to the securities of this company")
      @RequestParam(required = false) final UUID companyId) {
    return securityService.findAll(companyId);
  }

  /**
   *
   * @param id security identifier
   * @return the security
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('SECURITY_LIST')")
  @Operation(summary = "Get a security", description = "Requires the SECURITY_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The security"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the SECURITY_LIST permission"),
      @ApiResponse(responseCode = "404", description = "Security not found")
  })
  public SecurityDto findById(@PathVariable final UUID id) {
    return securityService.findById(id);
  }

  /**
   *
   * @param request the security data
   * @return the created security
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAuthority('SECURITY_CREATE')")
  @Operation(summary = "Create a security", description = "Requires the SECURITY_CREATE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Security created"),
      @ApiResponse(responseCode = "400", description = "Invalid data"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the SECURITY_CREATE permission"),
      @ApiResponse(responseCode = "404", description = "Company not found"),
      @ApiResponse(responseCode = "409", description = "ISIN or ticker already used")
  })
  public SecurityDto create(@Valid @RequestBody final SecurityRequestDto request) {
    return securityService.create(request);
  }

  /**
   *
   * @param id security identifier
   * @param request the security data
   * @return the updated security
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('SECURITY_UPDATE')")
  @Operation(summary = "Update a security", description = "Requires the SECURITY_UPDATE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Security updated"),
      @ApiResponse(responseCode = "400", description = "Invalid data, or the issuing company changed"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the SECURITY_UPDATE permission"),
      @ApiResponse(responseCode = "404", description = "Security not found"),
      @ApiResponse(responseCode = "409", description = "ISIN or ticker already used")
  })
  public SecurityDto update(@PathVariable final UUID id, @Valid @RequestBody final SecurityRequestDto request) {
    return securityService.update(id, request);
  }

  /**
   *
   * @param id security identifier
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAuthority('SECURITY_DELETE')")
  @Operation(summary = "Delete a security", description = "Requires the SECURITY_DELETE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Security deleted"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the SECURITY_DELETE permission"),
      @ApiResponse(responseCode = "404", description = "Security not found"),
      @ApiResponse(responseCode = "409", description = "The security is still used (prices)")
  })
  public void delete(@PathVariable final UUID id) {
    securityService.delete(id);
  }
}
