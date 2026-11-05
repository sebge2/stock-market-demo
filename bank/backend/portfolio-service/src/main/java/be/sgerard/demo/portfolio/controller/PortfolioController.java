package be.sgerard.demo.portfolio.controller;

import be.sgerard.demo.portfolio.config.OpenApiConfiguration;
import be.sgerard.demo.portfolio.model.dto.PortfolioDto;
import be.sgerard.demo.portfolio.model.dto.PortfolioRequestDto;
import be.sgerard.demo.portfolio.service.PortfolioService;
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
 * Exposes CRUD endpoints for portfolios.
 *
 */
@RestController
@RequestMapping("/portfolios")
@Tag(name = "Portfolios", description = "Management of portfolios")
@SecurityRequirement(name = OpenApiConfiguration.OAUTH2_SCHEME)
@Validated
public class PortfolioController {

  /**
   *
   * Portfolio service.
   *
   */
  private final PortfolioService portfolioService;

  /**
   *
   * Creates the controller.
   *
   * @param portfolioService the portfolio service
   *
   */
  public PortfolioController(PortfolioService portfolioService) {
    this.portfolioService = portfolioService;
  }

  /**
   *
   * Returns a page of portfolios.
   *
   * @param page zero-based page number
   * @param size number of portfolios per page
   * @return the requested portfolio page
   *
   */
  @Operation(summary = "List the portfolios", description = "Requires the PORTFOLIO_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "A page of portfolios"),
      @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the PORTFOLIO_LIST permission")
  })
  @PreAuthorize("hasAuthority('PORTFOLIO_LIST')")
  @GetMapping
  public Page<PortfolioDto> findAll(@RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
    return portfolioService.findAll(PageRequest.of(page, size, Sort.by("createdAt").ascending()));
  }

  /**
   *
   * Returns a portfolio.
   *
   * @param id the portfolio identifier
   * @return the portfolio
   *
   */
  @Operation(summary = "Get a portfolio by identifier", description = "Requires the PORTFOLIO_LIST permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The portfolio"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the PORTFOLIO_LIST permission"),
      @ApiResponse(responseCode = "404", description = "Portfolio not found")
  })
  @PreAuthorize("hasAuthority('PORTFOLIO_LIST')")
  @GetMapping("/{id}")
  public PortfolioDto findById(@PathVariable UUID id) {
    return portfolioService.findById(id);
  }

  /**
   *
   * Creates a portfolio.
   *
   * @param request the portfolio request
   * @return the created portfolio
   *
   */
  @Operation(summary = "Create a portfolio", description = "Requires the PORTFOLIO_CREATE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Portfolio created"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the PORTFOLIO_CREATE permission"),
      @ApiResponse(responseCode = "409", description = "The client already owns a portfolio")
  })
  @PreAuthorize("hasAuthority('PORTFOLIO_CREATE')")
  @PostMapping
  public ResponseEntity<PortfolioDto> create(@Valid @RequestBody PortfolioRequestDto request) {
    final PortfolioDto portfolio = portfolioService.create(request);
    return ResponseEntity.created(URI.create("/portfolios/" + portfolio.getId())).body(portfolio);
  }

  /**
   *
   * Updates a portfolio.
   *
   * @param id the portfolio identifier
   * @param request the portfolio request
   * @return the updated portfolio
   *
   */
  @Operation(summary = "Update a portfolio", description = "Requires the PORTFOLIO_UPDATE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Portfolio updated"),
      @ApiResponse(responseCode = "400", description = "Invalid request"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the PORTFOLIO_UPDATE permission"),
      @ApiResponse(responseCode = "404", description = "Portfolio not found"),
      @ApiResponse(responseCode = "409", description = "The client already owns another portfolio")
  })
  @PreAuthorize("hasAuthority('PORTFOLIO_UPDATE')")
  @PutMapping("/{id}")
  public PortfolioDto update(@PathVariable UUID id, @Valid @RequestBody PortfolioRequestDto request) {
    return portfolioService.update(id, request);
  }

  /**
   *
   * Deletes a portfolio.
   *
   * @param id the portfolio identifier
   *
   */
  @Operation(summary = "Delete a portfolio", description = "Requires the PORTFOLIO_DELETE permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Portfolio deleted"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the PORTFOLIO_DELETE permission"),
      @ApiResponse(responseCode = "404", description = "Portfolio not found"),
      @ApiResponse(responseCode = "409", description = "The portfolio still holds positions")
  })
  @PreAuthorize("hasAuthority('PORTFOLIO_DELETE')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    portfolioService.delete(id);
  }
}
