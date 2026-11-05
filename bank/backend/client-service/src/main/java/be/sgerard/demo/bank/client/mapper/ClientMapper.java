package be.sgerard.demo.bank.client.mapper;

import be.sgerard.demo.bank.client.model.Client;
import be.sgerard.demo.bank.client.model.dto.ClientDto;
import be.sgerard.demo.bank.client.model.dto.ClientRequestDto;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 *
 * Maps client entities to their API representations.
 *
 */
@Component
public class ClientMapper {

  /**
   *
   * Creates a client entity from request data and the identifier of the user created for it.
   *
   * @param request the client request DTO
   * @param userId the user-service identifier
   * @return the client entity
   *
   */
  public Client toEntity(ClientRequestDto request, UUID userId) {
    return new Client(request.getFirstName(), request.getLastName(), userId);
  }

  /**
   *
   * Updates a client entity with the mutable request data.
   *
   * @param request the client request DTO
   * @param client the client entity to update
   *
   */
  public void updateEntity(ClientRequestDto request, Client client) {
    client.setFirstName(request.getFirstName());
    client.setLastName(request.getLastName());
  }

  /**
   *
   * Converts a client entity to a response DTO.
   *
   * @param client the client entity
   * @return the client DTO
   *
   */
  public ClientDto toDto(Client client) {
    final ClientDto dto = new ClientDto();
    dto.setId(client.getId());
    dto.setUserId(client.getUserId());
    dto.setFirstName(client.getFirstName());
    dto.setLastName(client.getLastName());
    dto.setCreatedAt(client.getCreatedAt());
    dto.setUpdatedAt(client.getUpdatedAt());
    return dto;
  }
}
