package be.sgerard.demo.bank.client.model.dto;

import be.sgerard.demo.bank.client.model.ClientType;

import be.sgerard.demo.bank.client.model.ClientStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * Public representation of a client without credential data.
 *
 */
@Schema(name = "Client")
public class ClientDto {

  /**
   *
   * Type of the client.
   *
   */
  @Schema(description = "Type of the client")
  private ClientType type;

  /**
   *
   * Status of the client.
   *
   */
  @Schema(description = "Status of the client")
  private ClientStatus status;

  /**
   *
   * Client identifier.
   *
   */
  private UUID id;

  /**
   *
   * Identifier of the user in the user service.
   *
   */
  @Schema(description = "Identifier of the user automatically created in the user service")
  private UUID userId;

  /**
   *
   * First name.
   *
   */
  private String firstName;

  /**
   *
   * Last name.
   *
   */
  private String lastName;

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
   * @return the identifier
   *
   */
  public UUID getId() {
    return id;
  }

  /**
   *
   * @param id the identifier
   *
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   *
   * @return the user-service identifier
   *
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   *
   * @param userId the user-service identifier
   *
   */
  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  /**
   *
   * @return the first name
   *
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   *
   * @param firstName the first name
   *
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   *
   * @return the last name
   *
   */
  public String getLastName() {
    return lastName;
  }

  /**
   *
   * @param lastName the last name
   *
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
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
   * @return the type
   *
   */
  public ClientType getType() {
    return type;
  }

  /**
   *
   * @param type the type
   *
   */
  public void setType(ClientType type) {
    this.type = type;
  }

  /**
   *
   * @return the status
   *
   */
  public ClientStatus getStatus() {
    return status;
  }

  /**
   *
   * @param status the status
   *
   */
  public void setStatus(ClientStatus status) {
    this.status = status;
  }
}
