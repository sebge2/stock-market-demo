package be.sgerard.demo.bank.stock.controller;

import be.sgerard.demo.bank.stock.config.OpenApiConfiguration;
import be.sgerard.demo.bank.stock.model.dto.PortfolioValuationDto;
import be.sgerard.demo.bank.stock.model.dto.QuotationDto;
import be.sgerard.demo.bank.stock.model.external.PortfolioDto;
import be.sgerard.demo.bank.stock.service.ValuationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * REST endpoints exposing portfolio valuations and stock quotations.
 *
 */
@RestController
@Tag(name = "Valuation", description = "Portfolio valuation and stock quotations")
@SecurityRequirement(name = OpenApiConfiguration.OAUTH2_SCHEME)
@Validated
public class ValuationController {

  /**
   *
   * Accepted format of a stock symbol.
   *
   */
  private static final String SYMBOL_PATTERN = "^[A-Za-z0-9._-]{1,20}$";

  /**
   *
   * Business service.
   *
   */
  private final ValuationService valuationService;

  /**
   *
   * Creates a new instance.
   *
   * @param valuationService the business service
   *
   */
  public ValuationController(ValuationService valuationService) {
    this.valuationService = valuationService;
  }

  /**
   *
   * Values a portfolio.
   *
   * @param portfolio the portfolio to value, with its positions
   * @return the valuation
   *
   */
  @Operation(summary = "Value a portfolio",
      description = "Requires the PORTFOLIO_VALUATION_READ permission. The caller also needs the permissions "
          + "required by the exchange service, as the caller's token is forwarded.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The valuation of the portfolio"),
      @ApiResponse(responseCode = "400", description = "Invalid portfolio"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the PORTFOLIO_VALUATION_READ permission"),
      @ApiResponse(responseCode = "422", description = "A quotation is missing or too old"),
      @ApiResponse(responseCode = "500", description = "A required service is unavailable")
  })
  @PreAuthorize("hasAuthority('PORTFOLIO_VALUATION_READ')")
  @PostMapping("/portfolios/value")
  public PortfolioValuationDto valuate(@RequestBody PortfolioDto portfolio) {
    return valuationService.valuate(portfolio);
  }

  /**
   *
   * Retrieves the latest quotation of a stock.
   *
   * @param stockSymbol symbol of the stock
   * @return the quotation
   *
   */
  @Operation(summary = "Get the latest quotation of a stock", description = "Requires the QUOTATION_READ permission.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "The latest quotation"),
      @ApiResponse(responseCode = "400", description = "Invalid stock symbol"),
      @ApiResponse(responseCode = "401", description = "Not authenticated"),
      @ApiResponse(responseCode = "403", description = "Missing the QUOTATION_READ permission"),
      @ApiResponse(responseCode = "404", description = "Quotation not found"),
      @ApiResponse(responseCode = "500", description = "A required service is unavailable")
  })
  @PreAuthorize("hasAuthority('QUOTATION_READ')")
  @GetMapping("/quotations/{stockSymbol}")
  public QuotationDto findQuotation(@Parameter(description = "Symbol of the stock")
      @PathVariable @Pattern(regexp = SYMBOL_PATTERN) String stockSymbol) {
    return valuationService.findQuotation(stockSymbol);
  }
}
