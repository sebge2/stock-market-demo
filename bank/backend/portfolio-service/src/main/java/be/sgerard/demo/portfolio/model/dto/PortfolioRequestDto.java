package be.sgerard.demo.portfolio.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 *
 * Payload used to create or replace a portfolio.
 *
 */
@Schema(name = "PortfolioRequest")
public class PortfolioRequestDto {

  /**
   *
   * Identifier of the owning client.
   *
   */
  @NotNull
  @Schema(description = "Identifier of the client owning the portfolio (a client owns at most one portfolio)")
  private UUID clientId;

  /**
   *
   * @return the identifier of the owning client
   *
   */
  public UUID getClientId() {
    return clientId;
  }

  /**
   *
   * @param clientId the identifier of the owning client
   *
   */
  public void setClientId(UUID clientId) {
    this.clientId = clientId;
  }
}
