package be.sgerard.demo.bank.client.model.dto;

import jakarta.validation.constraints.NotNull;

import be.sgerard.demo.bank.client.model.ClientType;

import be.sgerard.demo.bank.client.model.ClientStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * Payload used to create or replace a client. The linked user identifier is never provided by the
 * caller: on creation the user is created in the user service and its identifier is stored.
 *
 */
@Schema(name = "ClientRequest")
public class ClientRequestDto {

  /**
   *
   * Type of the client.
   *
   */
  @NotNull
  @Schema(description = "Type of the client")
  private ClientType type;

  /**
   *
   * Status of the client.
   *
   */
  @NotNull
  @Schema(description = "Status of the client")
  private ClientStatus status;

  /**
   *
   * First name.
   *
   */
  @NotBlank
  @Size(max = 100)
  private String firstName;

  /**
   *
   * Last name.
   *
   */
  @NotBlank
  @Size(max = 100)
  private String lastName;

  /**
   *
   * Email address of the user created in the user service. Required on creation, ignored on update
   * (the email is not stored by the client service).
   *
   */
  @Email
  @Size(max = 255)
  @Schema(description = "Required on creation (used to create the user), ignored on update")
  private String email;

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
   * @return the email address
   *
   */
  public String getEmail() {
    return email;
  }

  /**
   *
   * @param email the email address
   *
   */
  public void setEmail(String email) {
    this.email = email;
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
