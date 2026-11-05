package be.sgerard.demo.bank.client.config;

import static org.assertj.core.api.Assertions.assertThat;

import be.sgerard.demo.bank.client.model.Client;
import be.sgerard.demo.bank.client.model.ClientStatus;
import be.sgerard.demo.bank.client.model.ClientType;
import be.sgerard.demo.bank.client.repository.ClientRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * Verifies the local client reference data.
 *
 */
@SpringBootTest
@ActiveProfiles("local")
class LocalDataSqlTest {

  /**
   *
   * Client repository.
   *
   */
  @Autowired
  private ClientRepository clientRepository;

  /**
   *
   * Verifies the local clients reference users of the user service.
   *
   */
  @Test
  void loadsClientsWithUserReferences() {
    assertThat(clientRepository.findAll()).extracting(Client::getFirstName)
        .containsExactlyInAnyOrder("Alice", "Bob", "Claire");
    assertThat(clientRepository.findAll()).extracting(Client::getUserId)
        .contains(UUID.fromString("72e91c48-68b6-4de7-9a40-21b9b6224101"));
    assertThat(clientRepository.findAll()).extracting(Client::getType)
        .containsExactlyInAnyOrder(ClientType.INDIVIDUAL, ClientType.INDIVIDUAL, ClientType.COMPANY);
    assertThat(clientRepository.findAll()).extracting(Client::getStatus)
        .containsExactlyInAnyOrder(ClientStatus.ACTIVE, ClientStatus.ACTIVE, ClientStatus.INACTIVE);
  }
}
