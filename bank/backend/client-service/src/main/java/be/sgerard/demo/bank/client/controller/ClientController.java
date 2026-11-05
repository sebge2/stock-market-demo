package be.sgerard.demo.bank.client.controller;

import be.sgerard.demo.bank.client.config.OpenApiConfiguration;
import be.sgerard.demo.bank.client.model.dto.ClientDto;
import be.sgerard.demo.bank.client.model.dto.ClientRequestDto;
import be.sgerard.demo.bank.client.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
 * Exposes CRUD endpoints for clients.
 *
 */
@RestController
@RequestMapping("/clients")
@Tag(name = "Clients", description = "Management of clients")
@SecurityRequirement(name = OpenApiConfiguration.OAUTH2_SCHEME)
@Validated
public class ClientController {

  /**
   *
   * Client service.
   *
   */
  private final ClientService clientService;

  /**
   *
   * Creates the controller.
   *
   * @param clientService the client service
   *
   */
  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  /**
   *
   * Returns a page of clients.
   *
   * @param page zero-based page number
   * @param size number of clients per page
   * @return the requested client page
   *
   */
  @Operation(summary = "List the clients", description = "Requires the CLIENT_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "A page of clients"),
      @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the CLIENT_LIST permission")
  })
  @PreAuthorize("hasAuthority('CLIENT_LIST')")
  @GetMapping
  public Page<ClientDto> findAll(@RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
    return clientService.findAll(PageRequest.of(page, size, Sort.by("lastName", "firstName").ascending()));
  }

  /**
   *
   * Returns a client.
   *
   * @param id the client identifier
   * @return the client
   *
   */
  @Operation(summary = "Get a client by identifier", description = "Requires the CLIENT_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The client"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the CLIENT_LIST permission"),
      @ApiResponse(responseCode = "404", description = "Client not found")
  })
  @PreAuthorize("hasAuthority('CLIENT_LIST')")
  @GetMapping("/{id}")
  public ClientDto findById(@PathVariable UUID id) {
    return clientService.findById(id);
  }

  /**
   *
   * Creates a client and automatically creates the corresponding user in the user service.
   *
   * @param request the client request
   * @return the created client
   *
   */
  @Operation(summary = "Create a client",
      description = "Requires the CLIENT_CREATE permission. The caller's token is forwarded to the "
          + "user service, so the caller also needs the USER_CREATE permission. A user is created "
          + "automatically (username derived from the email, temporary password) and its identifier "
          + "is returned as userId. Nothing is persisted if the user cannot be created.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Client created"),
      @ApiResponse(responseCode = "400", description = "Invalid request (email is required) or user creation validation failed"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the CLIENT_CREATE permission"),
      @ApiResponse(responseCode = "409", description = "Username already exists"),
      @ApiResponse(responseCode = "500", description = "Unexpected error while creating the user"),
      @ApiResponse(responseCode = "502", description = "User service failed or refused the request"),
      @ApiResponse(responseCode = "503", description = "User service unreachable after retries")
  })
  @PreAuthorize("hasAuthority('CLIENT_CREATE')")
  @PostMapping
  public ResponseEntity<ClientDto> create(@Valid @RequestBody ClientRequestDto request) {
    final ClientDto client = clientService.create(request);
    return ResponseEntity.created(URI.create("/clients/" + client.getId())).body(client);
  }

  /**
   *
   * Updates a client.
   *
   * @param id the client identifier
   * @param request the client request
   * @return the updated client
   *
   */
  @Operation(summary = "Update a client", description = "Requires the CLIENT_UPDATE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Client updated"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the CLIENT_UPDATE permission"),
      @ApiResponse(responseCode = "404", description = "Client not found")
  })
  @PreAuthorize("hasAuthority('CLIENT_UPDATE')")
  @PutMapping("/{id}")
  public ClientDto update(@PathVariable UUID id, @Valid @RequestBody ClientRequestDto request) {
    return clientService.update(id, request);
  }

  /**
   *
   * Deletes a client.
   *
   * @param id the client identifier
   *
   */
  @Operation(summary = "Delete a client", description = "Requires the CLIENT_DELETE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Client deleted"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the CLIENT_DELETE permission"),
      @ApiResponse(responseCode = "404", description = "Client not found")
  })
  @PreAuthorize("hasAuthority('CLIENT_DELETE')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    clientService.delete(id);
  }
}
