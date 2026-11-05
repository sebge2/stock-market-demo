package be.sgerard.demo.portfolio.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 *
 * Public representation of a portfolio.
 *
 */
@Schema(name = "Portfolio")
public class PortfolioDto {

  /**
   *
   * Portfolio identifier.
   *
   */
  private UUID id;

  /**
   *
   * Identifier of the owning client.
   *
   */
  private UUID clientId;

  /**
   *
   * Creation date.
   *
   */
  private LocalDateTime createdAt;

  /**
   *
   * Last update date.
   *
   */
  private LocalDateTime updatedAt;

  /**
   *
   * Positions held in the portfolio.
   *
   */
  private List<PositionDto> positions;

  /**
   *
   * @return the portfolio identifier
   *
   */
  public UUID getId() {
    return id;
  }

  /**
   *
   * @param id the portfolio identifier
   *
   */
  public void setId(UUID id) {
    this.id = id;
  }

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

  /**
   *
   * @return the creation date
   *
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   *
   * @param createdAt the creation date
   *
   */
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   *
   * @return the last update date
   *
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   *
   * @param updatedAt the last update date
   *
   */
  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  /**
   *
   * @return the positions held in the portfolio
   *
   */
  public List<PositionDto> getPositions() {
    return positions;
  }

  /**
   *
   * @param positions the positions held in the portfolio
   *
   */
  public void setPositions(List<PositionDto> positions) {
    this.positions = positions;
  }
}
